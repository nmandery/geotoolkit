(ns geotoolkit.geojson.schema
  (:require
    [schema.core :as s] ))


(def CoordinateList s/Any) ; TODO

(def Geometry {
  :type s/Str
  :coordinates CoordinateList      
})

(def MultiGeometry {
  :type s/Str
  :geometries s/Any ; TODO
})

(def Feature { 
  :geometry (s/either Geometry MultiGeometry)
  :properties {
    s/Keyword s/Any
  }
  :type s/Str
})
