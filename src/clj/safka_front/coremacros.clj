(ns safka-front.coremacros
    (:require [clojure.java.io :as io]
              [clojure.edn :as edn]))


(def ^:private default-file "resources/configuration/default.edn")

(defn- load-config-file [file]
  (with-open [reader (io/reader file)]
    (edn/read (java.io.PushbackReader. reader))))

(defmacro get-config []
 (load-config-file default-file))


(defmacro testi []
  {:test "testi"})