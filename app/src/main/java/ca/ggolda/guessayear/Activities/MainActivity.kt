package ca.ggolda.guessayear.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import ca.ggolda.guessayear.R
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import android.widget.SeekBar




class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find Components
        val figureImageImageView = findViewById<ImageView>(R.id.img_figure)
        val figureNameTextView = findViewById<TextView>(R.id.txt_figure_name)
        val yearSeekBar = findViewById<SeekBar>(R.id.skbr_year)
        val yearEditText = findViewById<EditText>(R.id.edt_year)
        val eraTextView = findViewById<TextView>(R.id.txt_era)
        val guessButton = findViewById<Button>(R.id.btn_guess)

        // Set Components
        figureImageImageView.setImageDrawable(getResources().getDrawable(R.drawable.kahn_01))
        figureNameTextView.setText("Genghis Khan")


        // TODO: OnChangeListener for EditText so ProgressBar also responds to it
        // Set Year and Era Views
        yearEditText.setText("" + yearSeekBar.progress)
        setEraTextView()

        // Set SeekBar OnChangeListener
        yearSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            internal var progressChangedValue = yearSeekBar.progress

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                var normalizedProg = progress

                // Normalize for TextView
                if (normalizedProg < 0) {
                    normalizedProg = normalizedProg * -1
                }
                yearEditText.setText("" + normalizedProg)
                setEraTextView()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub -- play sound?
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Toast.makeText(this@MainActivity, "Touch Stopped at: " + progressChangedValue,
                        Toast.LENGTH_SHORT).show()
            }
        })

        // Set Guess ("Confirm") Button OnClickListener
        guessButton.setOnClickListener { guessPress() }



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
