(ns drawing.reframing
  (:require 
   [superstructor.re-frame.fetch-fx]
   [re-frame.core :as rf]
   [day8.re-frame.http-fx]
   [ajax.core :as ajax]))

(rf/reg-event-fx
 :handler-with-fetch
 (fn [{:keys [db]} _]
   {:fetch {:method                 :get
            ;:url                    "https://www.gazeta.pl"
           ; :url                    "https://api.github.com/orgs/day8"
            :url                    "http://localhost:8080/dates"
            :mode                   :no-cors
            :timeout                5000
           ; :response-content-types { "text/plain" :text}
            :on-success             [:good-fetch-result]
            :on-failure             [:bad-fetch-result]}}))


(rf/reg-event-db
 :good-fetch-result
 (fn [db [_ response]]
   (.log js/console "success")
   (.log js/console (str response))
   (assoc db :success-get response)))

(rf/reg-event-db
 :bad-fetch-result
 (fn [db [_ response]]
   (.log js/console "failure")
   (.log js/console (str response))
   (assoc db :fail-get response)))



            

(rf/reg-event-fx
 :get-to-insecure
 (fn [_world _]
   {:http-xhrio {:method          :get
                 :uri             "http://localhost:8080/dates"
                ; :params          data
                 :timeout         5000
                ; :format          (ajax/text-request-format)
                 :response-format (ajax/text-response-format {:keywords? true})
                 :on-success      [:success-get-result]
                 :on-failure      [:failure-get-result]}}))

(rf/reg-event-db
 :success-get-result 
 (fn [db [_ response]]
   (.log js/console "success")
   (.log js/console (str response))
   (assoc db :old-success-get response)))

(rf/reg-event-db
 :failure-get-result
 (fn [db [_ response]]
   (.log js/console "failure")
   (.log js/console (str response))
   (assoc db :old-failure-get response)))


(rf/reg-event-db
 :init-db
  (fn [_ _]
    {}
  )
)