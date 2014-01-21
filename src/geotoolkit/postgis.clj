(ns geotoolkit.postgis
 (:import com.vividsolutions.jts.io.WKBReader))


(defn- update-values [m f & args]
 (reduce (fn [r [k v]] (assoc r k (apply f v args))) {} m))

(defn- to-jts-geometry-with-reader [^WKBReader wkbreader value] 
  (if (instance? org.postgresql.util.PGobject value)
    (if (= (.getType value) "geometry")
      (. wkbreader (read (. WKBReader (hexToBytes (.getValue value)))))
      value)
    value))

(defn to-jts-geometry [value] 
  "convert an PGobject value to a JTS geometry if it is of the type 'geometry'
   otherwise the original value is returned"
  (let [wkbreader (WKBReader.)] 
    (to-jts-geometry-with-reader wkbreader value)))

(defn resultset-to-jts-geometries [resultset]
  "convert all convertible PostGIS values in the resultset to JTS geometries"
  (let [wkbreader (WKBReader.)] 
    (map
      (fn [row] (update-values row #(to-jts-geometry-with-reader wkbreader %))) resultset)))
