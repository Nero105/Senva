package com.example.senva

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MiCitaFragmento.newInstance] factory method to
 * create an instance of this fragment.
 */
class MiCitaFragmento : Fragment(), OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null
    private var citaLat: Double = -12.027
    private var citaLng: Double = -77.012
    private var citaDireccion: String = "Dpto: Lima\nProv: Lima\nDist: San Juan de Lurigancho"
    private var marker: Marker? = null
    private lateinit var tvInfoCita: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            citaLat = it.getDouble("lat", citaLat)
            citaLng = it.getDouble("lng", citaLng)
            citaDireccion = it.getString("direccion", citaDireccion)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mi_cita_fragmento, container, false)
        mapView = view.findViewById(R.id.mapViewCita)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        val fabCambiarUbicacion = view.findViewById<View>(R.id.fabCambiarUbicacion)
        val btnAbrirEnMaps = view.findViewById<View>(R.id.btnAbrirEnMaps)
        val tvUbicacionActual = view.findViewById<TextView>(R.id.tvUbicacionActual)
        val tvDpto = view.findViewById<TextView>(R.id.tvDpto)
        val tvProv = view.findViewById<TextView>(R.id.tvProv)
        val tvDist = view.findViewById<TextView>(R.id.tvDist)
        tvInfoCita = view.findViewById(R.id.tvDireccionCita)

        // Inicializar dirección
        tvDpto.text = "Dpto: Lima"
        tvProv.text = "Prov: Lima"
        tvDist.text = "Dist: San Juan de Lurigancho"
        tvInfoCita.text = "Dirección de la cita:"

        fabCambiarUbicacion.setOnClickListener {
            Snackbar.make(view, "Toca el mapa para seleccionar una nueva ubicación", Snackbar.LENGTH_SHORT).show()
        }

        btnAbrirEnMaps.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:$citaLat,$citaLng?q=$citaLat,$citaLng(Lugar de la cita)")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(mapIntent)
            } else {
                Snackbar.make(view, "No se encontró Google Maps", Snackbar.LENGTH_SHORT).show()
            }
        }

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val citaLatLng = LatLng(citaLat, citaLng)
        marker = googleMap?.addMarker(MarkerOptions().position(citaLatLng).title("Lugar de la cita"))
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(citaLatLng, 15f))
        marker?.showInfoWindow()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap?.isMyLocationEnabled = true
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (isAdded && location != null) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    googleMap?.addPolyline(
                        PolylineOptions()
                            .add(userLatLng, citaLatLng)
                            .color(ContextCompat.getColor(requireContext(), R.color.verde_claro))
                            .width(8f)
                    )
                    val bounds = LatLngBounds.builder()
                        .include(userLatLng)
                        .include(citaLatLng)
                        .build()
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                    // Mostrar badge si está cerca (menos de 500m)
                    val results = FloatArray(1)
                    android.location.Location.distanceBetween(
                        userLatLng.latitude, userLatLng.longitude,
                        citaLatLng.latitude, citaLatLng.longitude,
                        results
                    )
                    val tvUbicacionActual = view?.findViewById<TextView>(R.id.tvUbicacionActual)
                    if (results[0] < 500) {
                        tvUbicacionActual?.visibility = View.VISIBLE
                    } else {
                        tvUbicacionActual?.visibility = View.GONE
                    }
                }
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        // Permitir al usuario seleccionar la ubicación tocando el mapa
        googleMap?.setOnMapClickListener { latLng ->
            if (!isAdded) return@setOnMapClickListener
            marker?.remove()
            marker = googleMap?.addMarker(MarkerOptions().position(latLng).title("Nueva ubicación de la cita"))
            marker?.showInfoWindow()
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
            citaLat = latLng.latitude
            citaLng = latLng.longitude
            // Actualizar dirección usando Geocoder
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(citaLat, citaLng, 1)
            val tvDpto = view?.findViewById<TextView>(R.id.tvDpto)
            val tvProv = view?.findViewById<TextView>(R.id.tvProv)
            val tvDist = view?.findViewById<TextView>(R.id.tvDist)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                tvDpto?.text = "Dpto: ${addr.adminArea ?: "-"}"
                tvProv?.text = "Prov: ${addr.subAdminArea ?: "-"}"
                tvDist?.text = "Dist: ${addr.locality ?: addr.subLocality ?: "-"}"
            } else {
                tvDpto?.text = "Dpto: -"
                tvProv?.text = "Prov: -"
                tvDist?.text = "Dist: -"
            }
            if (isAdded) {
                Snackbar.make(requireView(), "Ubicación de la cita actualizada", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }
    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MiCitaFragmento.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(lat: Double, lng: Double, direccion: String) =
            MiCitaFragmento().apply {
                arguments = Bundle().apply {
                    putDouble("lat", lat)
                    putDouble("lng", lng)
                    putString("direccion", direccion)
                }
            }
    }
}