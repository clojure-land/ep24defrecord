(ns ep24defrecord.core
  (:require [clojure.test :refer [deftest]]))

(defrecord Book [author title year])

(Book. "Ursula K. Le Guin" "The Dispossessed" 1974)
(->Book "Ursula K. Le Guin" "The Dispossessed" 1974)

(.title (->Book "Ursula K. Le Guin" "The Dispossessed" 1974))

(map->Book {:author "Ursula K. Le Guin" :title "The Dispossessed" :year 1974})


(= (map->Book {:author "Pat Cadigan", :title "Fools", :year 1992})
   (map->Book {:author "Pat Cadigan", :title "Fools", :year 1992}))             ;;=> true

(= (map->Book {:author "Pat Cadigan", :title "Fools", :year 1992})
   (map->Book {:author "Anne McCaffrey", :title "Dragonflight", :year 1968}))   ;;=> false

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(-> (map->Book {:title "Consider Phlebas" :author "Iain M. Banks" :year 1987})
    (with-meta {:source "Wikipedia"})
    meta)
;;=> {:source "Wikipedia"}

;; doesn't work
^{:source "Wikipedia"} (map->Book {:title "Consider Phlebas" :author "Iain M. Banks" :year 1987})


#ep24defrecord.core.Book["Iain M. Banks" "Consider Phlebas" 1987]

(def consider-phlebas
  ^{:source "Wikipedia"}
  #ep24defrecord.core.Book{:author "Iain M. Banks"
                           :title "Consider Phlebas"
                           :year 1987})

(meta consider-phlebas)
;;=> {:source "Wikipedia"}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def book (->Book "Iain M. Banks" "Consider Phlebas" 1987))

(get book :title)         ;;=> "Consider Phlebas"
(:title book)             ;;=> "Consider Phlebas"

(assoc book :title "The Player of Games")
;;=> #ep24defrecord.core.Book{:author "Iain M. Banks", :title "The Player of Games", :year 1987}

(-> book
    (assoc :title "The Player of Games")
    (update :year inc))
;;=> #ep24defrecord.core.Book{:author "Iain M. Banks", :title "The Player of Games", :year 1988}


(assoc book :series "The Culture Series")
;;=> #ep24defrecord.core.Book{:author "Iain M. Banks", :title "Consider Phlebas", :year 1987, :series "The Culture Series"}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(seq book)
;;=> ([:author "Iain M. Banks"] [:title "Consider Phlebas"] [:year 1987])

(map first book)          ;;=> (:author :title :year)
(map key book)            ;;=> (:author :title :year)
(map val book)            ;;=> ("Iain M. Banks" "Consider Phlebas" 1987)

(into {} book)
;;=> {:author "Iain M. Banks", :title "Consider Phlebas", :year 1987}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord Book [author title year]
  clojure.lang.IFn
  (invoke [this kw]
    (get this kw)))


(def book (->Book "Iain M. Banks" "Consider Phlebas" 1987))

(book :title)
;;=> "Consider Phlebas"

(get book :title)         ;;=> "Consider Phlebas"
(:title book)             ;;=> "Consider Phlebas"

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defprotocol Shape
  "A protocol for computing properties of geometrical shapes."
  (surface-area [shape]
    "The surface area of this shape."))

(:doc (meta #'Shape))     ;;=> "A protocol for computing properties of geometrical shapes."
(:doc (meta #'surface-area));;=> "The surface area of this shape."

(defrecord Rectangle [width height]
  Shape
  (surface-area [rect]
    (* width height)))

(surface-area (->Rectangle 2 3))
;;=> 6

(defrecord Circle [radius]
  Shape
  (surface-area [circle]
    (* Math/PI radius radius)))

(surface-area (->Circle 1))
;;=> 3.141592653589793

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defprotocol Shape
  "A protocol for computing properties of geometrical shapes."
  (surface-area [shape]
    "The surface area of this shape."))

(extend-type java.awt.Rectangle
  Shape
  (surface-area [rect]
    (* (.width rect) (.height rect))))

(surface-area (java.awt.Rectangle. 100 50)) ;;=> 5000

(extend-protocol Shape
  java.awt.Rectangle
  (surface-area [rect]
    (* (.width rect) (.height rect)))

  java.awt.geom.Ellipse2D$Double
  (surface-area [e]
    (* Math/PI (.width e) (.height e))))

(surface-area (java.awt.geom.Ellipse2D$Double. 0 0  100 50))
 ;;=> 15707.963267948966

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defprotocol UserService
  (fetch-user-info [_ user-id] "Fetch info about the given user"))

;; app
(defrecord RESTUserService [endpoint]
  UserService
  (fetch-user-info [_ user-id]
    (http-get (str endpoint "/" user-id))))

(defn user-profile-handler [api user-id]
  ,,,)

(user-profile-handler (->RESTUserService "http://...") 32)


;; test
(defrecord TESTUserService [mock-data]
  UserService
  (fetch-user-info [_ user-id]
    (get mock-data user-id)))

(deftest profile-test
  (is (= (user-profile-handler (->TESTUserService {32 {:name "John"}}) 32) ,,,)))
