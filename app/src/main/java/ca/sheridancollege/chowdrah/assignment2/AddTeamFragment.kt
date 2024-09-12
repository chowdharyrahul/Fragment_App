package ca.sheridancollege.chowdrah.assignment2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import ca.sheridancollege.chowdrah.assignment2.databinding.FragmentAddTeamBinding
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddTeamFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddTeamFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var binding: FragmentAddTeamBinding? = null
    val firebaseFirestore = FirebaseFirestore.getInstance()
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddTeamBinding.inflate(inflater, container, false)
        addcountrySpinner()
        binding?.button?.setOnClickListener { saveTeamToFirestore() }

        return binding?.root
        // return inflater.inflate(R.layout.fragment_add_team, container, false)
    }

    private fun addcountrySpinner() {

        val operations = arrayOf(
            "Select Continent",
            "South America",
            "Europe",
            "Africa",
            "Asia",
            "North America"
        )
        val arrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, operations)
        val operationList = binding?.spinner
        operationList?.adapter = arrayAdapter

        operationList?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> operationList?.setSelection(0)
                    1 -> operationList?.setSelection(1)
                    2 -> operationList?.setSelection(2)
                    3 -> operationList?.setSelection(3)
                    4 -> operationList?.setSelection(4)
                    5 -> operationList?.setSelection(5)
                    else -> ""
                }

            }


            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

    }

    private fun saveTeamToFirestore() {
        val team = hashMapOf(
            "countryName" to binding?.editTextText?.text.toString(),
            "continentName" to binding?.spinner?.selectedItem.toString(),
            "gamesPlayed" to binding?.editTextText3?.text.toString().toInt(),
            "gamesWon" to binding?.editTextText4?.text.toString().toInt(),
            "gamesDrawn" to binding?.editTextText5?.text.toString().toInt(),
            "gamesLost" to binding?.editTextText6?.text.toString().toInt()
        )

        firebaseFirestore.collection("user")
            .add(team)
            .addOnSuccessListener { documentReference ->
                // Handle success
                // For example, show a success message
                binding?.editTextText?.text?.clear()
                binding?.editTextText3?.text?.clear()
                binding?.editTextText4?.text?.clear()
                binding?.editTextText5?.text?.clear()
                binding?.editTextText6?.text?.clear()
                Toast.makeText(requireContext(), "Data Entered Successfully", Toast.LENGTH_SHORT)
                    .show()


                // For example, show a success messag
            }
            .addOnFailureListener { e ->
                // Handle failure
                // For example, show an error message
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddTeamFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddTeamFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}