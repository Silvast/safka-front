(ns safka-front.events
  (:require
   [re-frame.core :as re-frame]
   [reagent.core :as r]
   [safka-front.config :as config]
   [safka-front.db :as db]
   [day8.re-frame.http-fx]  
   [ajax.core :refer [json-request-format json-response-format]]
   [day8.re-frame.tracing :refer-macros [fn-traced]]))

;; (def host (.. js/window -location -host))
;; ;;(def receipt-url (str host "/api/receipts"))
;; (def receipt-url "/api/receipts")
;; (def receipt-url "http://localhost:3000/api/receipts")
;; (defonce log (.log js/console receipt-url))


(defn get-receipt-url []
  (if config/debug?
    "http://localhost:3000/api/receipts"
     "/api/receipts"
    ))

  (def receipt-url (get-receipt-url))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
   db/default-db))

(re-frame/reg-event-db
  ::set-navigation-mode-mobile
  (fn-traced [db]
    (assoc db :navigation-mode :mobile)))

(re-frame/reg-event-db
  ::set-navigation-mode-desktop
  (fn-traced [db]
    (assoc db :navigation-mode :desktop)))  

(re-frame/reg-event-db
  ::set-active-tab
  (fn-traced [db [_ active-tab]]
    (assoc db :active-tab active-tab)))

(re-frame/reg-event-db
  ::set-drawer-open
  (fn-traced [db]
    (assoc db :drawer-open true)))

(re-frame/reg-event-db
  ::set-drawer-closed
  (fn-traced [db]
    (assoc db :drawer-open false)))

(re-frame/reg-event-db
  ::set-add-receipt-data
  (fn-traced [db [_ key-path new-value]]
    (assoc-in db [:add-receipt-data key-path] new-value)))

(re-frame/reg-event-db
  ::set-advanced-receipt-data
  (fn-traced [db [_ new-value]]
    (assoc-in db [:advanced-data] new-value)))


(re-frame/reg-event-db
  ::set-advanced-receipt-data-value
  (fn-traced [db [_ n new-value]]
    (assoc-in db [:advanced-data n :number] new-value)))

(re-frame/reg-event-fx
  ::get-receipt
  (fn-traced [{:keys [db]} [_ number]]
    {:http-xhrio {:method          :get
                  :uri             (str receipt-url "/random?number=" number )
                  :headers                {"Accept" "application/json", "Content-Type" "application/json"}
                  :response-format (json-response-format {:keywords? true})
                  :on-success      [:get-receipt-success]
                  :on-failure      [:api-request-error :get-receipt]}
      :db         (assoc-in db [:loading :receipt] true)}))

(re-frame/reg-event-db
  :get-receipt-success
  (fn-traced [db [_ result]]
        (-> db
            (assoc-in [:loading :receipt] false)
            (assoc :receipt-api-response result))))

(defn get-ids-url [ids]
(let [params (r/atom [])]  
  (doseq [id ids]
  (if (= id (first ids))
    (swap! params conj (str "id=" id))
    (swap! params conj (str "&id="id))))
  (str receipt-url "/ingredients?" (clojure.string/join @params))))

(re-frame/reg-event-fx
  ::get-ingredients
  (fn-traced [{:keys [db]} [_ ids]]
    {:http-xhrio {:method          :get
                  :uri             (get-ids-url ids)
                  :response-format (json-response-format {:keywords? true})
                  :on-success      [:get-ingredients-success]
                  :on-failure      [:api-request-error :get-ingredients]}
      :db         (assoc-in db [:loading :ingredients] true)}))

(re-frame/reg-event-db
  :get-ingredients-success
  (fn-traced [db [_ result]]
        (-> db
            (assoc-in [:loading :ingredients] false)
            (assoc :receipt-ingredients-response result))))

(re-frame/reg-event-fx
  ::get-receipt-list
  (fn-traced [{:keys [db]} [_ wishes]]
    {:http-xhrio {:method          :post
                  :uri             (str receipt-url "/list")
                  :body            (.stringify js/JSON (clj->js wishes))
                  :request-content-type   :json
                  :headers                {"Accept" "application/json", "Content-Type" "application/json"}
                  :response-format (json-response-format {:keywords? true})
                  :on-success      [:get-receipt-list-success]
                  :on-failure      [:api-request-error :get-receipt-list]}
      :db         (assoc-in db [:loading :receipt-list] true)}))

(re-frame/reg-event-db
  :get-receipt-list-success
  (fn-traced [db [_ result]]
        (-> db
            (assoc-in [:loading :receipt-list] false)
            (assoc :receipt-list-response result))))


(defn format-ingredients-data [data]
 (let [ingredients (:ingredients data)
       ingredients-list (clojure.string/split ingredients #",")
       new-ingredients (mapv #(assoc {} :name %) ingredients-list)]
   (.stringify js/JSON (clj->js
   (assoc-in data [:ingredients] new-ingredients)))))

(re-frame/reg-event-fx
  ::post-receipt
 (fn-traced [{:keys [db]} [_]]
    {:http-xhrio {:method          :post
                  :header          "header 'Content-Type: application/json'"
                  :uri             (str receipt-url "/add-new")
                  :body            (format-ingredients-data (:add-receipt-data db))
                  :request-content-type   :json
                  :headers                {"Accept" "application/json", "Content-Type" "application/json"}
                  :response-format (json-response-format {:keywords? true})
                  :on-success      [:post-receipt-success]
                  :on-failure      [:api-request-error :post-receipt]}
      :db         (assoc-in db [:loading :add-receipt] true)}))

(re-frame/reg-event-db
  :post-receipt-success
  (fn-traced [db [_ result]]
        (-> db
            (assoc-in [:loading :add-receipt] false)
            (assoc :add-receipt-response result))))

(re-frame/reg-event-db
  :clear-add-receipt
  (fn-traced [db]
        (-> db
            (dissoc [:loading :add-receipt])
            (dissoc :add-receipt-response)
            (dissoc :add-receipt-data)
            (dissoc :loading))))

(re-frame/reg-event-fx
  ::get-search-result
  (fn-traced [{:keys [db]} [_ name]]
    {:http-xhrio {:method          :get
                  :uri             (str receipt-url "/search?name=" name )
                  :headers                {"Accept" "application/json", "Content-Type" "application/json"}
                  :response-format (json-response-format {:keywords? true})
                  :on-success      [:get-search-success]
                  :on-failure      [:api-request-error :get-search-result]}
      :db         (assoc-in db [:loading :search-result] true)}))

(re-frame/reg-event-db
  :get-search-success
  (fn-traced [db [_ result]]
        (-> db
            (assoc-in [:loading :search-result] false)
            (assoc :search-result result))))

(re-frame/reg-event-db
  ::set-search-name
  (fn-traced [db [_ name]]
    (assoc db :search-name name)))