(defproject geotoolkit "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.vividsolutions/jts "1.11"]
                 [org.clojure/java.jdbc "0.3.2"]
                 [ring/ring-jetty-adapter "1.1.6"] 
                 [postgresql "9.1-901.jdbc4"]
                 [org.clojure/data.json "0.2.4"]])
