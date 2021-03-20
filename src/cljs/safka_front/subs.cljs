(ns safka-front.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::active-tab
 (fn [db _]
   (:active-tab db)))

 (re-frame/reg-sub
 ::add-receipt-data
 (fn [db key-path]
   (get-in db [:add-receipt-data key-path]))) 

 (re-frame/reg-sub
 ::get-foodtype
 (fn [db]
   (get-in db [:add-receipt-data :food-type])))

 (re-frame/reg-sub
 ::get-instructions
 (fn [db]
   (get-in db [:add-receipt-data :instructions])))

 (re-frame/reg-sub
 ::get-receipt-name
 (fn [db]
   (get-in db [:add-receipt-data :name])))
 
(re-frame/reg-sub
 ::get-ingredients-list
 (fn [db]
   (get-in db [:add-receipt-data :ingredients])))

(re-frame/reg-sub
  ::receipt-api-response
  (fn [db _]
    (:receipt-api-response db)))

(re-frame/reg-sub
  ::receipt-ingredients-response
  (fn [db _]
    (:receipt-ingredients-response db)))

(re-frame/reg-sub
  ::add-receipt-response
  (fn [db]
   (get-in db [:add-receipt-response])))

 (re-frame/reg-sub
  ::get-advanced-receipt-data
  (fn [db n]
    (get-in db [:advanced-data n :number])))

 (re-frame/reg-sub
  ::get-advanced-data-all
  (fn [db]
    (db :advanced-receipt-data)))

 (re-frame/reg-sub
  ::get-advanced-data
  (fn [db]
    (db :advanced-data)))

(re-frame/reg-sub
  ::receipt-list-response
  (fn [db]
   (get-in db [:receipt-list-response])))