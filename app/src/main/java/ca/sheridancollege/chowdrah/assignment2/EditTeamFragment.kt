package ca.sheridancollege.chowdrah.assignment2

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.SearchView
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditTeamFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditTeamFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var searchView: SearchView
    private lateinit var scrollView: ScrollView
    private lateinit var searchResultsContainer: LinearLayout

    private val firestore = FirebaseFirestore.getInstance()
    private val collectionReference = firestore.collection("user")

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
        //return inflater.inflate(R.layout.fragment_edit_team, container, false)
        val view = inflater.inflate(R.layout.fragment_edit_team, container, false)

        searchView = view.findViewById(R.id.searchView)
        scrollView = view.findViewById(R.id.scrollView)
        searchResultsContainer = view.findViewById(R.id.searchResultsContainer)

        // Set up search view listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchForTeams(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchForTeams(newText)
                return true
            }
        })

        return view
    }

    private fun searchForTeams(query: String?) {
        // Clear existing views
        searchResultsContainer.removeAllViews()

        // Check if query is not empty
        if (!query.isNullOrEmpty()) {
            // Construct a Firestore query to search for documents where countryName field matches the query
            val searchQuery = collectionReference
                .orderBy("countryName")
                .startAt(query)
                .endAt(query + "\uf8ff") // Ensures that the query matches all documents starting with the query

            // Execute the query
            searchQuery.get()
                .addOnSuccessListener { documents ->
                    // Iterate through the search results
                    for (document in documents) {
                        val countryName = document.getString("countryName")
                        // Create a button for each search result
                        // Display country name with an EDIT button
                        val textView = TextView(requireContext()).apply {
                            text = countryName
                        }
                        val editButton = Button(requireContext()).apply {
                            text = "EDIT"
                            setOnClickListener {
                                // Handle edit button click here
                                // For example, navigate to a new fragment for editing details
                                // You can pass the team name and other details as arguments
                                val teamName = countryName ?: ""
                                displayTeamDetails(teamName)
                            }
                        }

                        val layout = LinearLayout(requireContext()).apply {
                            orientation = LinearLayout.HORIZONTAL
                            gravity = Gravity.CENTER_HORIZONTAL
                            addView(textView)
                            addView(editButton)
                        }
                        searchResultsContainer.addView(layout)
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                    Log.e("Search", "Error searching for teams: $exception")
                }
        }
    }

    private fun displayTeamDetails(teamName: String) {
        searchResultsContainer.removeAllViews()

        val teamQuery = collectionReference.whereEqualTo("countryName", teamName)

        teamQuery.get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]

                    val countryName = document.getString("countryName") ?: ""
                    val continentName = document.getString("continentName") ?: ""
                    val gamesPlayed = document.getLong("gamesPlayed") ?: 0
                    val gamesWon = document.getLong("gamesWon") ?: 0
                    val gamesDrawn = document.getLong("gamesDrawn") ?: 0
                    val gamesLost = document.getLong("gamesLost") ?: 0

                    val countryNameTextView = TextView(requireContext())
                    countryNameTextView.text = "Country Name: $countryName"

                    val continentNameTextView = TextView(requireContext())
                    continentNameTextView.text = "Continent: $continentName"

                    val gamesPlayedTextView = TextView(requireContext())
                    gamesPlayedTextView.text = "Games Played: $gamesPlayed"

                    val gamesWonTextView = TextView(requireContext())
                    gamesWonTextView.text = "Games Won: $gamesWon"

                    val gamesDrawnTextView = TextView(requireContext())
                    gamesDrawnTextView.text = "Games Drawn: $gamesDrawn"

                    val gamesLostTextView = TextView(requireContext())
                    gamesLostTextView.text = "Games Lost: $gamesLost"


                    searchResultsContainer.addView(countryNameTextView)
                    searchResultsContainer.addView(continentNameTextView)
                    searchResultsContainer.addView(gamesPlayedTextView)
                    searchResultsContainer.addView(gamesWonTextView)
                    searchResultsContainer.addView(gamesDrawnTextView)

                }
            }
            .addOnFailureListener { exception ->
                Log.e("DisplayTeamDetails", "Error fetching team details: $exception")
            }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditTeamFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            EditTeamFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}