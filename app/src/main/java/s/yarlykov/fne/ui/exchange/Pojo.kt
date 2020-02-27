package s.yarlykov.fne.ui.exchange

class Pojo {

    var name : String? = null
    var age : Int? = null
    var kids : List<String>? = null
    var kidsAge : List<Int>? = null

    override fun toString(): String {
        return "name=$name, age=$age, kids=$kids, kids_age=$kidsAge"
    }
}