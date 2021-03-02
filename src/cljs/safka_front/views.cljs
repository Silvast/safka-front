(ns safka-front.views
  (:require
   [reagent.core :as r]
   [re-frame.core :as re-frame]
   [clojure.string :as str]
   [safka-front.subs :as subs]
   [safka-front.events :as events]
   [reagent-material-ui.colors :as colors]
   [reagent-material-ui.core.grid :refer [grid]]
   [reagent-material-ui.core.app-bar :refer [app-bar]]
   [reagent-material-ui.core.paper :refer [paper]]
   [reagent-material-ui.core.typography :refer [typography]]
   [reagent-material-ui.core.button :refer [button]]
   [reagent-material-ui.core.text-field :refer [text-field]]
   [reagent-material-ui.core.select :refer [select]]
   [reagent-material-ui.core.menu-item :refer [menu-item]]
   [reagent-material-ui.core.form-control :refer [form-control]]
   [reagent-material-ui.core.input-label :refer [input-label]]
   [reagent-material-ui.core.tabs :refer [tabs]]
   [reagent-material-ui.core.tab :refer [tab]]
   [reagent-material-ui.icons.add-box :refer [add-box]]
   [reagent-material-ui.styles :as styles]))

(def custom-theme
  {
  ;;  :palette {:primary   colors/yellow
  ;;            :secondary colors/green}
   :typography {:font-family "Roboto" :font-size "14"}})

(defn event-value
  [^js/Event e]
  (.. e -target -value))

(defonce  number-of-receipts (r/atom 2))

(defonce receipt-data (r/atom {:name "" :food-type "" :instructions "" :ingredients [{:name "juusto"}]}))

(defonce ingredients (r/atom ""))


(defn show-receipts [data]
 [:div 
  [:div (map (fn [item] 
               [:div {:key (:id item)} 
                [:h2 (:name item)]
                [:p (:instructions item)]]) (:result data))]])

(defn show-ingredients [data]
 [:div 
  [:h2 "Ostoslista"]
  [:div (map (fn [item] 
               [:div {:key (inc (.indexOf (:result data) item))} 
                [:p item]]) (:result data))]])

(defn get-ids [data]
         (map #(:id %) (:result data)))

(defn get-panel []
   (let [receipt-api-response
         (re-frame/subscribe [::subs/receipt-api-response])
         receipt-ingredients-response
         (re-frame/subscribe [::subs/receipt-ingredients-response])]
     [:div  {:role "tabpanel"}
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
               [menu-item {:value item} (str item)]) (range 1 8))]]]

    [:p   [button {:variant "contained" :color "secondary" :on-click #(re-frame/dispatch [::events/get-receipt @number-of-receipts])} "hae reseptit"]]
      [:div [show-receipts @receipt-api-response]]
      [button {:variant "contained" :color "secondary" :on-click #(re-frame/dispatch [::events/get-ingredients (get-ids @receipt-api-response)])} "Hae ostoslista"]
      [show-ingredients @receipt-ingredients-response]]))


;; (defn change-ingredients [ingredients]
;;  (let [ingredients-list (clojure.string/split ingredients #",")]
;;    (if (> (count ingredients-list) 2)
;;     (swap! receipt-data assoc-in [:ingredients 1 :name] "sieni")
;;     (js/console.log (:name (second (:ingredients @receipt-data))))
;;     ;(js/console.log (count ingredients-list))
;;      )))

(def advanced-data (r/atom [{:food-type "eines" :number 0}
                            {:food-type  "vleines" :number 0}
                            {:food-type  "uuniruoka" :number 0}
                            {:food-type  "keitto" :number 0}
                            {:food-type  "perusruoka" :number 0}
                            {:food-type  "pasta" :number 0}]))

(defn select-component [label n food-type]
  [:div
   [:p
    [form-control {:variant "outlined" :class-name "form-control"}
     [input-label {:id "demo-simple-select-outlined-label"} label]
     [select
      {:label-id "demo-simple-select-outlined-label"
       :id "demo-simple-select-outlined"
       :label label
       :value (get-in @advanced-data [n :number])
       :on-change (fn [e]
                     (do
                      (swap! advanced-data assoc n {:food-type food-type :number (event-value e)})
                       (re-frame/dispatch [::events/set-advanced-receipt-data @advanced-data])))}
      (map (fn [item]
             [menu-item {:value item} (str item)]) (range 0 7))]]]])

(defn advanced-panel []
(let [list-response (re-frame/subscribe [::subs/receipt-list-response])]
(if (some? @list-response)
 [:div 
  [:h2 "Tässä ruokalistasi!"]
  (map (fn [item] 
         [:p 
          [:h3 (:name item)]
          [:p (:instructions item)]]) 
       (:food-list (:result @list-response)))
  [:h2 "Tässä ostoslistasi"]
  (map (fn [item] 
         [:p item]) 
       (:ingredients-list (:result @list-response)))
  [:p {:id "submitbutton2"}
    [button {:variant "contained" :color "secondary" :on-click #(do (re-frame/dispatch [::events/initialize-db]) (re-frame/dispatch [::events/set-active-tab :advanced-panel]))} "Hae uudestaan!"]]
  
  ] 
 [:div
 [:h2 "Valitse, minkä tyyppisiä ruokia haluat hakea:"]
 [:div
  [:p
   [select-component "EINEKSET" 0 "eines"]
   [select-component "VLEINES" 1 "vleines"]
   [select-component "UUNIRUOKA" 2 "uuniruoka"]
   [select-component "KEITTO" 3 "keitto"]
   [select-component "PERUSRUOKA" 4 "perusruoka"]
   [select-component "PASTA" 5 "pasta"]]
  [:p {:id "button"}
    [button {:variant "contained" :color "secondary" :on-click #(re-frame/dispatch [::events/get-receipt-list @advanced-data])} "Hae ruokalista"]]]])))
  

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
    [button {:variant "contained" :color "secondary" :on-click #(do (re-frame/dispatch [::events/initialize-db]) (re-frame/dispatch [::events/set-active-tab :insert-tab]))} "Siirry lisäämään uusi resepti"]]]
  [:div {:role "tabpanel"}
   [:h2 "Syötä uusi resepti"]
   [:form
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
        {:value "Uuniruoka"} "Uuniruoka"]
       [menu-item
        {:value "Keitto"} "Keitto"]
       [menu-item
        {:value "Pasta"} "Pasta"]
       [menu-item
        {:value "Uuniruoka"} "Uuniruoka"]]]]
   [:p {:id "button"}
    [button {:variant "contained" :color "secondary" :on-click #(re-frame/dispatch [::events/post-receipt])} "Luo uusi resepti"]]]])))


(defn resepti-panel []
   (let [active-tab (re-frame/subscribe [::subs/active-tab])]
         [:div
          [app-bar
           {:position "static"}
           [tabs
            {:value 
             (cond 
               (= @active-tab :get-tab) 0
               (= @active-tab :advanced-panel) 1
               (= @active-tab :insert-tab) 2
               )}
            [tab
             {:label "Hae"
              :on-click #(re-frame/dispatch [::events/set-active-tab :get-tab])
              :wrapped true
              }]
            [tab
             {:label "Listahaku"
              :on-click #(re-frame/dispatch [::events/set-active-tab :advanced-panel])
              :wrapped true}]
            [tab
             {:label "Syötä resepti"
              :on-click #(re-frame/dispatch [::events/set-active-tab :insert-tab])
              :wrapped true}]]]
          (condp = @active-tab
            :get-tab [get-panel]
            :advanced-panel [advanced-panel]
            :insert-tab [insert-panel])]))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [styles/theme-provider (styles/create-mui-theme custom-theme)
      [:div {:class-name "classes.root"}
       [grid
        {:container true
         :direction "row"
         :justify "center"}
        [grid
         {:item true
          :xs 8}
         [:h1 ""]
         [resepti-panel]]]]]))
