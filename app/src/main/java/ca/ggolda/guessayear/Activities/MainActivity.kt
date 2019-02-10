package ca.ggolda.guessayear.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ca.ggolda.guessayear.R
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import android.widget.SeekBar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO: REMOVE: You don't have to find view by id anymore! Kotlin rules.

        // Set Components
        img_figure.setImageDrawable(getResources().getDrawable(R.drawable.kahn_01))
        txt_figure_name.setText("Genghis Khan")



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

                Log.e("hey","setYear: " + yearInt)

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

    fun guessPress() {
        val myToast = Toast.makeText(this, "Guess: " + edt_year.text + " " + txt_era.text, Toast.LENGTH_LONG)
        myToast.show()
    }

    fun setEraTextView() {
        if (skbr_year.progress >= 0) {
            txt_era.setText("AD")
        } else {
            txt_era.setText("BC")
        }
    }

}

data class FigureModel(var id: String, var name: String, var imgSrc: String, var figureDescription: String,
                       var birthYr: Int, var deathYr: Int, var exactBirth: Boolean, var exactDeath: Boolean,
                       var eraText: String)
