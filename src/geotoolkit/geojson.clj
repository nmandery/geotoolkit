(ns geotoolkit.geojson
  (:require [clojure.data.json :as json]
            [ring.util.response :as response]))


(defn- dump-coordinate [^com.vividsolutions.jts.geom.Coordinate c]
  (let [pt [(.x c) (.y c)]]
    (if (not (Double/isNaN (.z c))) 
      (conj pt (.z c))
      pt)))

(defn- dump-jts-polygon [^com.vividsolutions.jts.geom.Polygon poly]
  (let [ext-ring (map dump-coordinate (.getCoordinates (.getExteriorRing poly)))
        n-rings (.getNumInteriorRing poly)]
    (if (> n-rings 0)
      (concat [ext-ring] (map (fn [n] (map dump-coordinate (.getCoordinates (.getInteriorRingN poly n)))) (range 0 n-rings)))
      [ext-ring])))

(defn- dump-jts-point [^com.vividsolutions.jts.geom.Geometry geom]
   (dump-coordinate (first (.getCoordinates geom))))

(defn- dump-jts-line [^com.vividsolutions.jts.geom.Geometry geom]
  (map dump-coordinate (.getCoordinates geom)))

(defn- dump-jts-multi [dump-fn ^com.vividsolutions.jts.geom.Geometry geom] 
  (map dump-fn (map #(.getGeometryN geom %) (range 0 (.getNumGeometries geom)))))

(defn- dump-jts-simple-geometry [^com.vividsolutions.jts.geom.Geometry geom]
  (case (.getGeometryType geom)
    "Point" (dump-jts-point geom)
    "LineString" (dump-jts-line geom)
    "Polygon" (dump-jts-polygon geom)
    "MultiPoint" (dump-jts-multi dump-jts-point geom)
    "MultiLineString" (dump-jts-multi dump-jts-line geom)
    "MultiPolygon" (dump-jts-multi dump-jts-polygon geom)
    (throw (java.lang.IllegalArgumentException. (str "can not dump geometry type " (.getGeometryType geom))))))

(defn dump-jts-geometry [^com.vividsolutions.jts.geom.Geometry geom]
  (when (not (nil? geom))
    (let [geometry {:type (.getGeometryType geom)}]
      (if (instance? com.vividsolutions.jts.geom.GeometryCollection geom)
        (assoc geometry :geometries (dump-jts-multi dump-jts-geometry geom))
        (assoc geometry :coordinates (dump-jts-simple-geometry geom))
        ))))

(defn- dump-feature [m geomkey]
  (-> {:type "Feature"}
      (assoc :geometry (dump-jts-geometry (get m geomkey)))
      (assoc :properties (dissoc m geomkey))))


(defn to-geojson-structure [v geomkey]
  "convert a featurecollection or feature with jts geometries to a
   clojure structure build like an geojson"
  (if (seq? v)
    (-> {:type "FeatureCollection"}
      (assoc :features (map #(dump-feature % geomkey) v)))
    (dump-feature v geomkey)) )

(defn to-geojson [v geomkey & jsonoptions]
  "convert a featurecollection or feature with jts geometries to a
  geojson string"
  (apply json/write-str (to-geojson-structure v geomkey) jsonoptions))

(defn ring-json-response [structure req-params]
  "return a clojure structure as json/json-p response
  from a ring handler"
  (let [json (json/write-str structure)
        resp (response/status {} 200)
        callback (get req-params "callback")]
    (if (nil? callback)
      (-> resp
          (response/content-type "application/json")
          (assoc :body json))
      (-> resp
          (response/content-type "application/javascript")
          (assoc :body (str callback "(" json ");")))
          )))

(defn ring-jts-geojson-response [features geom-key req-params]
  "return a featurecollection/feature with JTS geometries as geojson
  from a ring handler"
  (ring-json-response (to-geojson-structure features geom-key)))
