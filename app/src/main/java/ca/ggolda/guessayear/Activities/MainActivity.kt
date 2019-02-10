package ca.ggolda.guessayear.Activities

import android.content.Context
import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ca.ggolda.guessayear.R
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import android.widget.SeekBar
import android.text.Editable
import android.text.TextWatcher
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var figuresList: List<FigureModel>
    var totalListItems: Int = 0
    var displayIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Generate Data
        figuresList = generateDummyList().data
        totalListItems = figuresList.size

        // Pick Random Item From List
        displayIndex = grabRandomFromList().index

        // Set Components
        img_figure.setImageDrawable(getResources().getDrawable(resIdByName( figuresList[displayIndex].imgSrc, "drawable")))




        txt_figure_name.setText(figuresList[displayIndex].name)



        // Set Year and Era Views
        edt_year.setText("" + skbr_year.progress)
        setEraTextView()

        // Set YearText OnChangeListener
        edt_year.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                val setYear = s.toString()
                val yearInt: Int
                if (setYear != "") {
                    yearInt = setYear.toInt()
                } else {
                    yearInt = 99999
                }



                // TODO: only works for AD right now. Hacked together.
                if (yearInt in 0..2019) {
                    if (yearInt != skbr_year.progress && yearInt != skbr_year.progress * -1) {
                        skbr_year.setProgress(yearInt)
                    }
                }

            }
        })

        // Set SeekBar OnChangeListener
        skbr_year.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            internal var progressChangedValue = skbr_year.progress

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                var normalizedProg = progress

                // Normalize for TextView
                if (normalizedProg < 0) {
                    normalizedProg = normalizedProg * -1
                }
                if (edt_year.text.toString() != "" + normalizedProg) {
                    edt_year.setText("" + normalizedProg)
                }
                setEraTextView()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub -- play sound?
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Event on LetGo -- might not need
            }
        })

        // Set Guess ("Confirm") Button OnClickListener
        btn_guess.setOnClickListener { guessPress() }


    }

    private fun guessPress() {
        if (skbr_year.progress in figuresList[displayIndex].birthYr..figuresList[displayIndex].deathYr) {
            val myToast = Toast.makeText(this, "Guess: " + edt_year.text + " " + txt_era.text +" is RIGHT!", Toast.LENGTH_LONG)
            myToast.show()
        } else {
            val myToast = Toast.makeText(this, "Guess: " + edt_year.text + " " + txt_era.text +" is WRONG!", Toast.LENGTH_LONG)
            myToast.show()
        }


    }

    fun setEraTextView() {
        if (skbr_year.progress >= 0) {
            txt_era.setText("AD")
        } else {
            txt_era.setText("BC")
        }
    }

    private fun generateDummyList() = object {
        val person0 = FigureModel("0","Genghis Khan", "genghis_khan_1227", "was around.", 1162, 1227,
                10, 0)
        val person1 = FigureModel("1","Walt Disney", "walt_disney_1966", "was around.", 1901, 1966,
                0, 0)
        val person2 = FigureModel("2","Socrates", "socrates_399n", "was around.", -470, -399,
                10, 0)
        val person3 = FigureModel("3","Dan Brown", "dan_brown_9999", "was around.", 1964, 9999,
                0, 0)
        val data: List<FigureModel> = listOf(person0, person1, person2, person3)
    }

    private fun grabRandomFromList() = object {
        // Get Random Item based on available range
        var r = Random()
        val randInt = r.nextInt(totalListItems)
        val index: Int = randInt
    }

    private fun resIdByName(resIdName: String?, resType: String): Int {
        resIdName?.let {
            return resources.getIdentifier(it, resType, packageName)
        }
        throw Resources.NotFoundException()
    }



}

data class FigureModel(var id: String, var name: String, var imgSrc: String, var figureDescription: String,
                       var birthYr: Int, var deathYr: Int, var birthError: Int, var deathError: Int)
