package edu.temple.browsr

data class Page (var title: String, var url: String){
    constructor() : this("_blank", "")
}