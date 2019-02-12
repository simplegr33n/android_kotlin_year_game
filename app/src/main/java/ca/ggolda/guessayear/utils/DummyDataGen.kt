package ca.ggolda.guessayear.utils

import ca.ggolda.guessayear.data.FigureModel

object DummyDataGen {

    fun genDummyList() : List<FigureModel> {
        val person0 = FigureModel("0", "Genghis Khan", "genghis_khan_1227", "was around.", 1162, 1227,
                10, 0)
        val person1 = FigureModel("1", "Walt Disney", "walt_disney_1966", "was around.", 1901, 1966,
                0, 0)
        val person2 = FigureModel("2", "Socrates", "socrates_399n", "was around.", -470, -399,
                10, 0)
        val person3 = FigureModel("3", "Dan Brown", "dan_brown_9999", "was around.", 1964, 9999,
                0, 0)
        val person4 = FigureModel("4", "Jacques Plante", "jacques_plante_1986", "popularized the goaltender mask in hockey.", 1929, 1986,
                0, 0)

        return listOf(person0, person1, person2, person3, person4)
    }
}