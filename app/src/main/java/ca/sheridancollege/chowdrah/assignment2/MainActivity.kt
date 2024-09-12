package ca.sheridancollege.chowdrah.assignment2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var fragmentManager: FragmentManager =
            supportFragmentManager
        var fragmentTransaction: FragmentTransaction
        fragmentTransaction = fragmentManager.beginTransaction()
        var fragment = HomeFragment()
        fragmentTransaction.replace(R.id.fragmentContainerView2, fragment)
        fragmentTransaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.mymenu, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.idItem0 -> {
                //Toast.makeText(this, "Item one was selected", Toast.LENGTH_SHORT).show()
                var fragmentManager: FragmentManager =
                    supportFragmentManager
                var fragmentTransaction: FragmentTransaction
                fragmentTransaction = fragmentManager.beginTransaction()
                var fragment = HomeFragment()
                fragmentTransaction.replace(R.id.fragmentContainerView2, fragment)
                fragmentTransaction.commit()
                return true

            }


            R.id.idItem1 -> {
                //Toast.makeText(this, "Item one was selected", Toast.LENGTH_SHORT).show()
                var fragmentManager: FragmentManager =
                    supportFragmentManager
                var fragmentTransaction: FragmentTransaction
                fragmentTransaction = fragmentManager.beginTransaction()
                var fragment = AddTeamFragment()
                fragmentTransaction.replace(R.id.fragmentContainerView2, fragment)
                fragmentTransaction.commit()
                return true

            }

            R.id.idItem2 -> {
                var fragmentManager: FragmentManager =
                    supportFragmentManager
                var fragmentTransaction: FragmentTransaction
                fragmentTransaction = fragmentManager.beginTransaction()
                var fragment = EditTeamFragment()
                fragmentTransaction.replace(R.id.fragmentContainerView2, fragment)
                fragmentTransaction.commit()
                return true

            }

            R.id.idItem3 -> {
                var fragmentManager: FragmentManager =
                    supportFragmentManager
                var fragmentTransaction: FragmentTransaction
                fragmentTransaction = fragmentManager.beginTransaction()
                var fragment = DeleteTeamFragment()
                fragmentTransaction.replace(R.id.fragmentContainerView2, fragment)
                fragmentTransaction.commit()
                return true

            }

            R.id.idItem4 -> {

                var fragmentManager: FragmentManager =
                    supportFragmentManager
                var fragmentTransaction: FragmentTransaction
                fragmentTransaction = fragmentManager.beginTransaction()
                var fragment = DisplayResultsFragment()
                fragmentTransaction.replace(R.id.fragmentContainerView2, fragment)
                fragmentTransaction.commit()
                return true

            }

            else -> super.onOptionsItemSelected(item)
        }


    }
}