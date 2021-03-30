(ns safka-front.views.searchpanel
  (:require
   [reagent.core :as r]
   [re-frame.core :as re-frame]
   [clojure.string :as str]
   [safka-front.subs :as subs]
   [safka-front.events :as events]
   [safka-front.views.components :as components]
   [reagent-material-ui.core.form-control :refer [form-control]]
   [reagent-material-ui.core.input-label :refer [input-label]]
   [reagent-material-ui.core.select :refer [select]]
   [reagent-material-ui.core.menu-item :refer [menu-item]]
   [reagent-material-ui.core.grid :refer [grid]]
   [reagent-material-ui.core.text-field :refer [text-field]]
   [reagent-material-ui.core.button :refer [button]]))

(defn event-value
  [^js/Event e]
  (.. e -target -value))

  (defonce search-name (r/atom ""))

(defn search-panel []
  (let [search-response
      (re-frame/subscribe [::subs/get-search-result])
      data (:result @search-response)]
    [:div {:class-name "panel-content"}
    (if (some? data)
    [:div [:h2 "Hakutulokset"]
    [components/show-receipts data]
    [:p {:id "submitbutton2"}
    [button {:variant "contained" :color "secondary" :on-click #(do (re-frame/dispatch [::events/initialize-db]) (re-frame/dispatch [::events/set-active-tab :search-panel]))} "Hae uudestaan!"]
    ]]
    [:div 
    [grid {:container true :spacing 4 :direction "row"} 
    [grid {:item  true}  
      [:form {:id "search-form"}
        [:p
          [text-field
          {:id "search-name"
            :variant "outlined"
            :label "nimi"
            :placeholder "makaronilaatikko"
            :value @(re-frame/subscribe [::subs/get-search-name])
            :on-change (fn [e]
                        (re-frame/dispatch [::events/set-search-name (event-value e)]))}]]]]
        [grid {:item true}
          [:p {:id "button"}
            [button {:variant "contained" :color "secondary" :on-click #(re-frame/dispatch [::events/get-search-result @(re-frame/subscribe [::subs/get-search-name])])} "Etsi nyt"]]]]])]))
