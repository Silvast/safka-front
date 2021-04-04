(ns safka-front.views.insertpanel
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
   [reagent-material-ui.core.text-field :refer [text-field]]
   [reagent-material-ui.core.button :refer [button]]))

(defn event-value
  [^js/Event e]
  (.. e -target -value))

(defn ingredients-list []
  (let [ingredients-list  @(re-frame/subscribe [::subs/get-ingredients-list])]
    [:p
     [text-field
      {:id "receipt-ingredient"
       :variant "outlined"
       :label "Ainesosat pilkulla erotettuna"
       :placeholder "esim. sipuli,maito,juusto"
       :value ingredients-list
       :timeout 500
       :on-change (fn [e] (re-frame/dispatch [::events/set-add-receipt-data :ingredients (event-value e)]))}]])) 

(defn insert-panel []
(let [post-response (re-frame/subscribe [::subs/add-receipt-response])]
(if (some? @post-response)
  [:div
   [:h2 "Reseptin lisääminen onnistui!"]
   [:div {:id "submitbutton"}
    [button {:variant "contained" :color "secondary" :on-click #(do (re-frame/dispatch [::events/initialize-db]) (re-frame/dispatch [::events/set-active-tab :insert-panel]))} "Siirry lisäämään uusi resepti"]]]
  [:div {:role "tabpanel"}
   [:div {:class-name "panel-content"}
    [:h2 "Syötä uusi resepti"]
   [:form {:id "insert-form"}
    [:p
     [text-field
      {:id "receipt-name"
       :variant "outlined"
       :label "nimi"
       :placeholder "makaronilaatikko"
       :value @(re-frame/subscribe [::subs/get-receipt-name])
       :on-change (fn [e]
                    (re-frame/dispatch [::events/set-add-receipt-data :name (event-value e)]))}]]
    [ingredients-list]
    [:p
     [text-field
      {:id "receipt-instructions"
       :variant "outlined"
       :label "Ohjeet valmistukseen"
       :multiline ""
       :rows 10
       :placeholder "Keitä 10min"
       :value @(re-frame/subscribe [::subs/get-instructions])
       :on-change (fn [e]
                    (re-frame/dispatch [::events/set-add-receipt-data :instructions (event-value e)]))}]]
    [:p
     [form-control {:variant "outlined" :class-name "form-control"}
      [input-label {:id "demo-simple-select-outlined-label"} "Ruokatyyppi"]
      [select
       {:label-id "demo-simple-select-outlined-label"
        :id "food-type"
        :label "Ruokalaji"
        :value @(re-frame/subscribe [::subs/get-foodtype])
        :on-change (fn [e]
                     (re-frame/dispatch [::events/set-add-receipt-data :food-type (event-value e)]))}
       [menu-item
        {:value "Eines"} "Eines"]
       [menu-item
        {:value "vleines"} "Viikonlopun lounaseines"]
       [menu-item
        {:value "Salaatti"} "Salaatti"]
       [menu-item
        {:value "Keitto"} "Keitto"]
       [menu-item
        {:value "Pasta"} "Pasta"]
       [menu-item
        {:value "Uuniruoka"} "Uuniruoka"]]]]
   [:p {:id "button"}
    [button {:variant "contained" :color "secondary" :on-click #(re-frame/dispatch [::events/post-receipt])} "Luo uusi resepti"]]]]])))
