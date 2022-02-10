(ns ^:figwheel-hooks drawing.core
  (:require
   [drawing.data :as data]
   [drawing.reframing :as rf-dr]
   ;---libraries
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [cljs.pprint :refer [pprint]]
   [re-frame.core :as rf]
   [re-frame.db :as rfdb]
  ))

 
(defn weather-render []
  [:div#weather-example {:style {:height "450px" :width "70%"}}])

(defn weather-did-mount []
  (.Line js/Morris (clj->js {:element "weather-example"
                             :data 
                             ;[{:day 30 :area 100 :extent 200} 
                             ;       {:day 31 :area 105 :extent 205}
                             ;       {:day 32 :area 95 :extent 180}]
                             data/baffin-30
                              :xkey "day"
                              :ykeys ["extent", "area"]
                             :labels ["ext" "areaa"]
                             })))

(defn morris-weather []
  (reagent/create-class {:reagent-render weather-render
                         :component-did-mount weather-did-mount}))


(def window-width (reagent/atom nil))

(defn draw-canvas-contents [canvas]
  (let [ctx (.getContext canvas "2d")
        w (.-clientWidth canvas)
        h (.-clientHeight canvas)]
    (.beginPath ctx)
    (.moveTo ctx 0 0)
    (.lineTo ctx w h)
    (.moveTo ctx w 0)
    (.lineTo ctx 0 h)
    (.stroke ctx)))

(defn div-with-canvas []
  (let [dom-node (reagent/atom nil)]
    (reagent/create-class
     {:component-did-update
      (fn [this]
        (draw-canvas-contents (.-firstChild @dom-node)))

      :component-did-mount
      (fn [this]
        (reset! dom-node (rdom/dom-node this)))

      :reagent-render
      (fn []
        @window-width
        [:div.with-canvas
         [:canvas (if-let [node @dom-node]
                    {:width (.-clientWidth node)
                     :height (.-clientHeight node)})]])})))

(defn on-window-resize [evt]
  (reset! window-width (.-innerWidth js/window)))

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Hello world!"})) 

(defn get-app-element []
  (gdom/getElement "app"))

(defn hello-world []
  [:div "Hello"
   [:div "here ere"]
   [div-with-canvas]
   [:div "Morris"]
   [:div "weather"]
   [morris-weather]
   [:pre (with-out-str (pprint @rfdb/app-db))]
   [:button {:on-click #(rf/dispatch [:handler-with-fetch ])} "kokoroko"]
   [:button {:on-click #(rf/dispatch [:get-to-insecure])} "old koko"] 
  ])

(defn mount [el]
  (rdom/render [hello-world] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
     (rf/dispatch-sync [:init-db])
	(mount el)
     (.addEventListener js/window "resize" on-window-resize)
    ))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^:after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
