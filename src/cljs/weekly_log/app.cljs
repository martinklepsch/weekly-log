(ns weekly-log.app
  (:require [rum.core :as rum]
            [mobiledoc-kit :as mdk]))

(rum/defcs editor < (rum/local nil ::editor) {:did-mount (fn [s]
                                                           (.render (mdk/Editor.) @(::editor s))
                                                           s)}
  [s text]
  [:div
   {:ref #(reset! (::editor s) %)}])

(defn init []
  (rum/mount (editor) (. js/document (getElementById "container"))))
