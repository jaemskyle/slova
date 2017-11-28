(ns nightcoders.muzt
  (:require [reagent.core :as r]
            [nightcoders.data :refer [slova]]
            [clojure.string :as cs]
            [cljs.core.async :refer [<! >!] :as a]
            [goog.net.XhrIo :as xhr])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))
  
(def clicks (r/atom 0))
(def words (r/atom slova))
(def vocabs (r/atom {}))

;; (def vocabs (r/atom (-> (.parse js/JSON (.getItem js/sessionStorage "yo"))
;;                         (js->clj :keywordize-keys true))))

(defn GET [url fnn]
  (xhr/send url
            (fn [event]
              (let [s (-> event .-target .getResponseText)]
                (fnn s)))))

(defn get-item [item fn]
  (let [href (aget js/window "location" "href")]
    (GET (str href item) fn)))


(get-item "backup - 2017:05:02-232203.json"
          (fn [s]
            (swap! vocabs
                   (fn [] (-> (.parse js/JSON s)
                        (js->clj :keywordize-keys true))))))

                        
                        
(def qword (r/atom ""))
(def col-selected (r/atom #{}))

(def style 
  (r/atom {:title {:color "red"
                   :text-decoration "none"
                   :list-style-type "none"}
           :list {:color "orange"}
           :card {:float "left"}}))

           
           

(defn info [i]
  [:h1 
   @i])


(defn word-item [w]
  [:span (:word w)])


(defn words-of-the-day [vocabulary]
  [:ul.wotd
   (for [i (:lists @vocabulary)]
        (let [item (get (:list i) (rand (count (:list i))))]
          (swap! col-selected (fn [] (conj @col-selected (:word item))))
          [:li
           ^{:key item}
           (:word item)]))])
  
  


(defn lists [lists]
   (let [item-style (r/atom {:color "#aaa"
                             :font-size "15px"
                             :list-style-type "none"})
         item-style-root (r/atom {:padding "0"})
         selected-word (get lists (rand (count lists)))]
         
     (fn []
       [:ul
        {:style @item-style-root}
        (doall (for [item lists]
                 ^{:key item} [:li.list-item
                               
                               {:on-click (fn [e]
                                            (aset e "target" "className" 
                                              (apply str (concat e.target.className " selected")))
                                            (swap! qword (fn [] (:word item))))
                                :class (if (or (= @qword (:word item)) (contains? @col-selected (:word item)))
                                           "selected")}
                                           
                               
                               [word-item item]]))])))
                               
  

(defn cards [cards]
  (let [cards-style (r/atom {})
        cards-style-root (r/atom {})]
    (fn []
      [:ul.card-holder 
       {:style @cards-style-root} 
       (doall (for [list (:lists @cards)]
                ^{:key list} [:li.cards 
                              
                              [:h4 {:style (:title @style)} (:name list)]  
                              [lists (:list list)]]))])))
                   
;;:on-mouse-over (fn [] (swap! item-style 
;;                (fn [] (assoc @item-style 
;;                :color "#222"
;;                :font-size "20px"))])))
;;:on-mouse-out (fn [] (swap! item-style 
;;                (fn [] (assoc @item-style 
;;                :color "#aaa"
;;                :font-size "15px"))])))

;;:on-mouse-over (fn []
;;                (swap! cards-style (fn [] (assoc @cards-style :background "#aaa")))))])))
;;:on-mouse-out (fn []
;;                (swap! cards-style (fn [] (assoc @cards-style :background "white")))))])))



(defn encr [w]
  (-> (str w)
      (cs/replace "а" "99")
      (cs/replace "ж" "9")
      (cs/replace "т" "1")
      (cs/replace "о" "3")
      (cs/replace "у" "2")
      (cs/replace "е" "12-")
      (cs/replace "й" "52")
      (cs/replace "ы" "66")
      (cs/replace "н" "32")))

    


(defn testing []
  (let [s (.getItem js/sessionStorage "yo")]
    (let [o (-> (.parse js/JSON s)
                (js->clj :keywordize-keys true)
                (:lists)
                (first)
                (:name))]

      (console.log (aget js/window "location" "href") @vocabs)
      
      [:div (aget js/window "location")])))


(defn testing-element []
  (let [s (str "backup - 2017:05:02-232203.json")
        href (aget js/window "location" "href")]

    [:div
     [:div (str href s)]
     [:div (.stringify js/JSON (aget js/window "location"))]]))





(defn content []
  [:div
   [words-of-the-day vocabs]     
   [info qword]
   [cards vocabs]])

(r/render-component [content] (.querySelector js/document "#content"))

