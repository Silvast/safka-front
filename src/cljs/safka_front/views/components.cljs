(ns safka-front.views.components
  (:require
   [reagent.core :as r]
   [re-frame.core :as re-frame] 
   [safka-front.events :as events]
   [safka-front.subs :as subs]
   [reagent-material-ui.core.card :refer [card]]
   [reagent-material-ui.core.menu-item :refer [menu-item]]
   [reagent-material-ui.core.form-control :refer [form-control]]
   [reagent-material-ui.core.input-label :refer [input-label]]
   [reagent-material-ui.core.select :refer [select]]))


(defn event-value
  [^js/Event e]
  (.. e -target -value))

(defn card-box [content id]
        [:div {:key (:id id) :class-name "card"} 
        [card {:variant "outlined"}
         [:div {:class-name "card-content"}
          content]]])


(def advanced-data (r/atom [{:food-type "eines" :number 0}
                            {:food-type  "vleines" :number 0}
                            {:food-type  "uuniruoka" :number 0}
                            {:food-type  "keitto" :number 0}
                            {:food-type  "perusruoka" :number 0}
                            {:food-type  "pasta" :number 0}]))

(defn select-component [label n food-type]
  (let [dropdown-value (re-frame/subscribe [::subs/get-add-receipt-data n])]
  [:div
   [:p
    [form-control {:variant "outlined" :class-name "form-control"}
     [input-label {:id "demo-simple-select-outlined-label"} label]
     [select
      {:label-id "demo-simple-select-outlined-label"
       :id "demo-simple-select-outlined"
       :label food-type
       :value @(re-frame/subscribe [::subs/get-add-receipt-data n])
       :on-change (fn [e]
                       (re-frame/dispatch [::events/set-advanced-receipt-data-value n (event-value e)]))}
      (map (fn [item]
             [menu-item {:value item} (str item)]) (range 0 7))]]]]))



(defn show-ingredients [receipt-list]
[:div 
  [:h2 "Ostoslista"]
  [:p (map (fn [item] 
              [:div {:key (inc (.indexOf receipt-list item))} 
                [:p item]]) receipt-list)]])

  (defn show-receipts [data]
  [:div 
    [:div (map (fn [item]
                [card-box 
                [:div [:h2 (:name item)]
                    [:p (:instructions item)]
                    [show-ingredients (:ingredients item)]] (:id item)]) data)]])