(ns safka-front.views.views
  (:require
   [reagent.core :as r]
   [re-frame.core :as re-frame]
   [safka-front.subs :as subs]
   [safka-front.events :as events]
   [safka-front.views.getpanel :as getpanel]
   [safka-front.views.advancedpanel :as advancedpanel]
   [safka-front.views.insertpanel :as insertpanel]
   [safka-front.views.searchpanel :as searchpanel]
   [reagent-material-ui.colors :as colors]
   [reagent-material-ui.core.grid :refer [grid]]
   [reagent-material-ui.core.app-bar :refer [app-bar]]
   [reagent-material-ui.core.toolbar :refer [toolbar]]
   [reagent-material-ui.core.drawer :refer [drawer]]
   [reagent-material-ui.icons.menu :refer [menu]]
   [reagent-material-ui.core.menu-item :refer [menu-item]]
   [reagent-material-ui.core.icon-button :refer [icon-button]]
   [reagent-material-ui.core.tabs :refer [tabs]]
   [reagent-material-ui.core.tab :refer [tab]]
   [reagent-material-ui.styles :as styles]))

(def custom-theme
  {
   :palette {:primary  {:main "#64FFDA"}
             :secondary {:main "#7C4DFF"}}
   :typography {:font-family "Roboto" :font-size "14"}})

(defn get-mobile-mode []
  (.log js/console js/window.innerWidth)
  (if (< js/window.innerWidth 1000)
  (re-frame/dispatch [::events/set-navigation-mode-mobile])
  (re-frame/dispatch [::events/set-navigation-mode-desktop])))   

(defn navigate [panel]
  (re-frame/dispatch [::events/set-active-tab panel])
  (re-frame/dispatch [::events/set-drawer-closed]))  

(defn mobile-navigation []
(let [drawer-open (re-frame/subscribe [::subs/drawer-state])]
[:div  [icon-button 
  {:color "inherit"
  :aria-label "Open drawer"
  :on-click #(re-frame/dispatch [::events/set-drawer-open])
  :edge "end"
  :class-name "menu-button"} 
  [menu]]
  [:nav 
    [drawer 
    {:variant "temporary"
     :open  @drawer-open
      :on-close #(re-frame/dispatch [::events/set-drawer-closed])}
      [menu-item 
        {:on-click #(navigate :get-panel)} "Hae"]
      [menu-item 
      {:on-click #(navigate :advanced-panel)} "Listahaku"]
      [menu-item 
      {:on-click #(navigate :insert-panel)} "Syötä resepti"]
      [menu-item 
      {:on-click #(navigate :search-panel)} "Etsi resepti"]
      ]]]))
 
(defonce tab-state (r/atom 0))

(defn handle-change [new-value]
  (.log js/console new-value)
  (reset! tab-state new-value)
    (cond 
    (= new-value 0) (re-frame/dispatch [::events/set-active-tab :get-panel])
    (= new-value 1) (re-frame/dispatch [::events/set-active-tab :advanced-panel])
    (= new-value 2) (re-frame/dispatch [::events/set-active-tab :advanced-panel])
    (= new-value 3) (re-frame/dispatch [::events/set-active-tab :search-panel])))

(defn event-value
  [^js/Event e]
  (.log js/console e)
  (.. e -target -value))



 (defn desktop-navigation []
  (let [active-tab (re-frame/subscribe [::subs/active-tab])
        value @tab-state]
    [tabs
    {:value @tab-state
     :indicator-color "secondary"}
    [tab
    {:label "Hae"
     :on-click #(handle-change 0)
       :wrapped true}]
    [tab
    {:label "Listahaku"
      :on-click #(handle-change 1)
      :index 1
      :wrapped true}]
    [tab
    {:label "Syötä resepti"
     :on-click #(handle-change 2)
      :wrapped true}]
    [tab
    {:label "Etsi resepti"
     :on-click #(handle-change 3)
      :wrapped true}]]))

(defn resepti-panel []
   (let [active-tab (re-frame/subscribe [::subs/active-tab])
         drawer-open (re-frame/subscribe [::subs/drawer-state])
         mode (get-mobile-mode)
         navigation-mode (re-frame/subscribe [::subs/navigation-mode])]
        
         [:div {:class-name "root"}
         [:nav
          [app-bar
           {:position "static"}
          ;; [toolbar
           (if (= @navigation-mode :mobile)
            (mobile-navigation)
            (desktop-navigation))]]
          (condp = @active-tab
            :get-panel [getpanel/get-panel]
            :advanced-panel [advancedpanel/advanced-panel]
            :insert-panel [insertpanel/insert-panel]
            :search-panel [searchpanel/search-panel])]))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [styles/theme-provider (styles/create-mui-theme custom-theme)
      [:div
       [grid
        {:container true
         :direction "row"
         :justify "center"}
        [grid
         {:item true
          :xs 10}
         [resepti-panel]]]]]))
