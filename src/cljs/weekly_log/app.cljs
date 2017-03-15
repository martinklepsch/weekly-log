(ns weekly-log.app
  (:require [rum.core :as rum]
            ;; [cljsjs.mobiledoc-kit]
            [mobiledoc-kit :as mdk]))

(rum/defcs editor < (rum/local nil ::editor) {:did-mount (fn [s]
                                                           (.render (mdk/Editor.) @(::editor s))
                                                           s)}
  [s text]
  [:div
   {:ref #(reset! (::editor s) %)}
   
   ])

(defn set-birthdate! [{:keys [day month year]}]
  (.setItem (.-localStorage js/window) "birthdate" (js/Date. year (dec month) day)))

(rum/defcs birthdate < (rum/local {} ::date)
  [s]
  [:div 
   [:input {:type "input" :placeholder "day" :on-change #(swap! (::date s) assoc :day (.. % -target -value))}]
   [:input {:type "input" :placeholder "month" :on-change #(swap! (::date s) assoc :month (.. % -target -value))}]
   [:input {:type "input" :placeholder "year" :on-change #(swap! (::date s) assoc :year (.. % -target -value))}]
   [:button {:on-click #(set-birthdate! @(::date s))} "Go"]])

(rum/defc app []
  [:div
   (birthdate)
   (editor)])

(defn init []
  (rum/mount (app) (. js/document (getElementById "container"))))
