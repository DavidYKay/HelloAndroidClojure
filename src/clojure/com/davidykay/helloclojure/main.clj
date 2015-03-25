(ns com.davidykay.helloclojure.main
  (:use [neko.activity :only [defactivity set-content-view!]]
        [neko.threading :only [on-ui]]
        [neko.ui :only [make-ui config]]
        )
  (:import (java.util Calendar)
           (android.app Activity)
           (android.app DatePickerDialog DatePickerDialog$OnDateSetListener)
           (android.app DialogFragment)))

(defn date-picker [activity]
  (proxy [DialogFragment DatePickerDialog$OnDateSetListener] []
    (onCreateDialog [savedInstanceState]
      (let [c (Calendar/getInstance)
            year (.get c Calendar/YEAR)
            month (.get c Calendar/MONTH)
            day (.get c Calendar/DAY_OF_MONTH)]
        (DatePickerDialog. activity this year month day)))
     (onDateSet [view year month day]
       (set-elmt ::date
         (format "%d%02d%02d" year (inc month) day)))))

(defn show-picker [activity dp]
  (. dp show (. activity getFragmentManager) "datePicker"))

(defn main-layout [activity]
  [:linear-layout {:orientation :vertical,
                   :id-holder :true,
                   :def `mylayout}
   [:edit-text {:hint "Event name",
                :id ::name}]
   [:edit-text {:hint "Event location",
                :id ::location}]
   [:linear-layout {:orientation :horizontal}
    [:text-view {:hint "Event date",
                 :id ::date}]
    [:button {:text "...",
              :on-click (fn [_] (show-picker activity
                                             (date-picker activity)))}]]
   [:button {:text "+ Event",
             :on-click (fn [_](add-event))}]
   [:text-view {:text @listing,
                :id ::listing}]])

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
  (swap! listing str 
     (apply format "%d - %s - %s\n" (map get-elmt [::date ::location ::name])))
  (update-ui))


(defactivity com.davidykay.helloclojure.MainActivity
  :key :main
  :on-create
  (fn [this bundle]
    (on-ui
     (set-content-view! (*a)
                        (main-layout this)))
    (on-ui
     (set-elmt ::listing @listing))))
