package ca.ggolda.guessayear.data

class FigureModel {

    var id: String
    var name: String
    var imgSrc: String
    var figureDescription: String
    var birthYr: Int
    var deathYr: Int
    var birthError: Int
    var deathError: Int

    constructor(id: String, name: String, imgSrc: String, figureDescription: String,
                birthYr: Int, deathYr: Int, birthError: Int, deathError: Int ) {
        this.id = id
        this.name = name
        this.imgSrc = imgSrc
        this.figureDescription = figureDescription
        this.birthYr = birthYr
        this.deathYr = deathYr
        this.birthError = birthError
        this.deathError = deathError
    }


}
