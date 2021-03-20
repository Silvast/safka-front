(ns safka-front.views.getpanel
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
   [reagent-material-ui.core.button :refer [button]]))

(defonce  number-of-receipts (r/atom 2))

(defn event-value
  [^js/Event e]
  (.. e -target -value))

(defn show-receipts [data]
 [:div 
  [:div (map (fn [item]
              [components/card-box 
               [:div [:h2 (:name item)]
                  [:p (:instructions item)]] (:id item)]) (:result data))]])

(defn show-ingredients [data]
 [:div 
  [:h2 "Ostoslista"]
  [components/card-box (map (fn [item] 
               [:div {:key (inc (.indexOf (:result data) item))} 
                [:p item]]) (:result data)) "shoppinglist"]])

(defn get-ids [data]
         (map #(:id %) (:result data)))

(defn get-panel []
   (let [receipt-api-response
         (re-frame/subscribe [::subs/receipt-api-response])
         receipt-ingredients-response
         (re-frame/subscribe [::subs/receipt-ingredients-response])]
     [:div  {:role "tabpanel"}
      [:div {:class-name "panel-content"}
      [:h2 "Hae lista satunnaisia reseptejä"]
      [:div
       [form-control {:variant "outlined" :class-name "form-control"}
        [input-label {:id "demo-simple-select-outlined-label"} "Reseptien määrä"]
        [select
         {:label-id "demo-simple-select-outlined-label"
          :id "demo-simple-select-outlined"
          :label "Määrä"
          :value @number-of-receipts
          :on-change (fn [e]
                     (reset! number-of-receipts (event-value e)))}
        (map (fn [item] 
               [menu-item {:value item :key item} (str item)]) (range 1 8))]]]

    [:p   [button {:variant "contained" :color "secondary" :on-click #(re-frame/dispatch [::events/get-receipt @number-of-receipts])} "hae reseptit"]]
      [:div [show-receipts @receipt-api-response]]
      [button {:variant "contained" :color "secondary" :on-click #(re-frame/dispatch [::events/get-ingredients (get-ids @receipt-api-response)])} "Hae ostoslista"]
      (if (some? @receipt-ingredients-response)
       [show-ingredients @receipt-ingredients-response])]]))
