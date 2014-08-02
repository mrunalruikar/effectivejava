; ============================================
; Linter
; ============================================

; For a lint report we want to know:
; * which element is affected
; * have a message describing the warning/error
; The element is always the first returned by the operation

(defrecord Check [operation params message])

(def checks
  [(Check. classesWithManyConstructorsOp {:threshold 4}
     "This class has too many constructors (#1#). Consider using static factory methods or the Builder pattern")
   (Check. constructorsWithManyParametersOp {:threshold 6}
     "This constructor has too many parameters (#1#). Consider using the Builder pattern")])

(defn replaceParamsInMessage [message result]
  (clojure.string/replace message "#1#" (toString (nth result 1))))

(defn printCheckResult [result message]
  (println (toString (nth result 0)) ":" (replaceParamsInMessage message result)))

(defn executeCheck [check cus]
  (let [operation (.operation check)
        params (.params check)
        threshold (:threshold params)
        results ((.query operation) {:cus cus :threshold threshold})
        message (.message check)]
    (doseq [r results]
      (printCheckResult r message))))

(defn linter [dir]
  (let [cus (cus dir)]
    (println "Loaded" (.size cus) "files")
    (doseq [c checks]
      (executeCheck c cus))))