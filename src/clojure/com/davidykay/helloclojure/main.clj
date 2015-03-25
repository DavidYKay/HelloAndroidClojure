(ns com.davidykay.helloclojure.main
  (:use [neko.activity :only [defactivity set-content-view!]]
        [neko.threading :only [on-ui]]
        [neko.ui :only [make-ui config]]
        )
  (:import (java.util Calendar)
           (android.app Activity)
           (android.app DatePickerDialog DatePickerDialog$OnDateSetListener)
           (android.app DialogFragment)))


(declare ^android.widget.LinearLayout mylayout)

(defn get-elmt [elmt]
  (str (.getText (elmt (.getTag mylayout)))))

(defn set-elmt [elmt s]
  (on-ui (config (elmt (.getTag mylayout)) :text s)))


(def listing (atom ""))

(defn update-ui []
  (set-elmt ::listing @listing)
  (set-elmt ::location "")
  (set-elmt ::name ""))

(defn add-event []
  (swap! listing str (get-elmt ::location) " - " 
           (get-elmt ::name) "\n")
  (update-ui))


(def main-layout [:linear-layout {:orientation :vertical,
                                  :id-holder true,
                                  :def `mylayout}
                  [:edit-text {:hint "Event name",
                               :id ::name}]
                  [:edit-text {:hint "Event location",
                               :id ::location}]
                  [:button {:text "+ Event",
                            :on-click (fn [_] (add-event))}]
                  [:text-view {:text @listing,
                              :id ::listing}]])


(defactivity com.davidykay.helloclojure.MainActivity
  :key :main
  :on-create
  (fn [this bundle]
    (on-ui
     (set-content-view! (*a)
                        main-layout))
    (on-ui
     (set-elmt ::listing @listing))))
