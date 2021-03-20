(ns safka-front.views.views
  (:require
   [reagent.core :as r]
   [re-frame.core :as re-frame]
   [safka-front.subs :as subs]
   [safka-front.events :as events]
   [safka-front.views.getpanel :as getpanel]
   [safka-front.views.advancedpanel :as advancedpanel]
   [safka-front.views.insertpanel :as insertpanel]
   [reagent-material-ui.colors :as colors]
   [reagent-material-ui.core.grid :refer [grid]]
   [reagent-material-ui.core.app-bar :refer [app-bar]]
   [reagent-material-ui.core.tabs :refer [tabs]]
   [reagent-material-ui.core.tab :refer [tab]]
   [reagent-material-ui.styles :as styles]))

(def custom-theme
  {
   :palette {:primary  {:main "#11cb5f"}
             :secondary {:main "#11cb5f"}}
   :typography {:font-family "Roboto" :font-size "14"}})

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
            :get-tab [getpanel/get-panel]
            :advanced-panel [advancedpanel/advanced-panel]
            :insert-tab [insertpanel/insert-panel])]))

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
