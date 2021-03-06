(ns safka-front.db)

(def default-db
  {:name "re-frame"
   :active-tab :get-panel
   :drawer-open false
   :navigation-mode :desktop
   :advanced-data [{:food-type "eines" :number 0}
                    {:food-type  "vleines" :number 0}
                    {:food-type  "uuniruoka" :number 0}
                    {:food-type  "keitto" :number 0}
                    {:food-type  "perusruoka" :number 0}
                    {:food-type  "pasta" :number 0}
                    {:food-type  "salaatti" :number 0}]})
