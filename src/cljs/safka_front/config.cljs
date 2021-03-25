(ns safka-front.config
  )

(def debug?
  ^boolean goog.DEBUG)


(def conf {:url (.. js/window -location -host)})