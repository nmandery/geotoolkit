(defproject geotoolkit "0.1.2"
  :description "A Clojure library designed to work with PostGIS, GeoJSON and related stuff."
  :url "https://github.com/nmandery/geotoolkit"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.vividsolutions/jts "1.11"]
                 [prismatic/schema "0.2.0"]
                 [ring/ring-core "1.2.1"]
                 [org.clojure/java.jdbc "0.3.2"]
                 [postgresql "9.1-901.jdbc4"]
                 [org.clojure/data.json "0.2.4"]])
