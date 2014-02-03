(ns geotoolkit.geojson.schema
  (:require
    [schema.core :as s] ))


(def Coordinate 
  (s/both
    [s/Num]
    (s/pred #(> (count %) 1) "has at least 2 coordinate numbers")))

(def CoordinateListLevel1 [Coordinate])
(def CoordinateListLevel2 [CoordinateListLevel1])
(def CoordinateListLevel3 [CoordinateListLevel2])

(def CoordinateList 
  (s/either CoordinateListLevel1 CoordinateListLevel2 CoordinateListLevel3))

(def BoundingBox 
  (s/both
    [s/Num]
    (s/pred #(= (count %) 4) "has exactly 4 coordinate numbers")))

(def CoordinateReferenceSystem {
  :type (s/both
    s/Str
    (s/pred 
      (fn [x] (some #(= x %) 
        ["name" "link"]))
      "has no valid CoordinateReferenceSystem type"))
  :properties {
    (s/either s/Str s/Keyword) s/Any
    }})

(def Geometry {
  :type (s/both
    s/Str
    (s/pred 
      (fn [x] (some #(= x %) 
        ["Point" "Polygon" "LineString"
        "MultiPoint" "MultiPolygon" "MultiLineString"]))
      "has geometry type"))
  :coordinates (s/either Coordinate CoordinateList)
  (s/optional-key :crs) CoordinateReferenceSystem
})

(def GeometryCollection {
  :type (s/both
    s/Str
    (s/pred 
      (fn [x] (some #(= x %) 
        [ "GeometryCollection"]))
      "has geometrycollection type"))
  :geometries [Geometry]
  (s/optional-key :crs) CoordinateReferenceSystem
})

(def Feature { 
  :geometry (s/either Geometry GeometryCollection)
  :properties {
    (s/either s/Str s/Keyword) s/Any
  }
  :type (s/both s/Str 
    (s/pred #(= % "Feature") "type of feature is 'Feature'"))
  (s/optional-key :bbox) BoundingBox
  (s/optional-key :crs) CoordinateReferenceSystem
})

(def FeatureCollection {
  :features [Feature]
  :type (s/both s/Str 
    (s/pred #(= % "FeatureCollection") "type of featurecollection is 'FeatureCollection'"))
  (s/optional-key :bbox) BoundingBox
  (s/optional-key :crs) CoordinateReferenceSystem
})
