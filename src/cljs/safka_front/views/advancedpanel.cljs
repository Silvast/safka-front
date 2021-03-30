(ns safka-front.views.advancedpanel
  (:require
   [reagent.core :as r]
   [re-frame.core :as re-frame]
   [clojure.string :as str]
   [safka-front.subs :as subs]
   [safka-front.events :as events]
   [safka-front.views.components :as components]
   [reagent-material-ui.core.card :refer [card]]
   [reagent-material-ui.core.grid :refer [grid]]
   [reagent-material-ui.core.button :refer [button]]))


(defn event-value
  [^js/Event e]
  (.. e -target -value))

(defn advanced-panel []
(let [list-response (re-frame/subscribe [::subs/receipt-list-response])]
  [:div {:class-name "panel-content"}
(if (some? @list-response)
 [:div
  [:h2 "Tässä ruokalistasi!"]
  (map (fn [item]
         [:div {:key (:id item) :class-name "card"}
          [card {:variant "outlined"}
           [:div {:class-name "card-content"}
            [:p
             [:h3 (:name item)]
             [:p (:instructions item)]]]]])
       (:food-list (:result @list-response)))
  [:h2 "Tässä ostoslistasi"]
  [components/card-box  (map (fn [item]
                     [:p item])
                  (:ingredients-list (:result @list-response))) "ostoslista"]
  [:p {:id "submitbutton2"}
   [button {:variant "contained" :color "secondary" :on-click #(do (re-frame/dispatch [::events/initialize-db]) (re-frame/dispatch [::events/set-active-tab :advanced-panel]))} "Hae uudestaan!"]]] 
 [:div
 [:h2 "Valitse, minkä tyyppisiä ruokia haluat hakea:"]
 [:div
  [:p
   [components/select-component "EINEKSET" 0 "eines"]
   [components/select-component "VLEINES" 1 "vleines"]
   [components/select-component "UUNIRUOKA" 2 "uuniruoka"]
   [components/select-component "KEITTO" 3 "keitto"]
   [components/select-component "PERUSRUOKA" 4 "perusruoka"]
   [components/select-component "PASTA" 5 "pasta"]]
  [:p {:id "button"}
    [button {:variant "contained" :color "secondary" :on-click #(re-frame/dispatch [::events/get-receipt-list @(re-frame/subscribe [::subs/get-advanced-data])])} "Hae ruokalista"]]]])]))