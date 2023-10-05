package com.sleep119.app

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import org.json.JSONArray

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UserFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        val modifyBtn = view.findViewById<TextView>(R.id.modifyUser)
        val addProtectorBtn = view.findViewById<ImageButton>(R.id.addProtectorBtn)
        val protectorLayout = view.findViewById<LinearLayout>(R.id.protectorLayout)

        UserService.getUser(requireContext(), 1) { res ->
            try {
                val testVal = res

                val name = view.findViewById<TextView>(R.id.userName)
                val blood = view.findViewById<TextView>(R.id.userBlood)
                val telno = view.findViewById<TextView>(R.id.userTelno)

                name.text = testVal.getString("name")
                blood.text = testVal.getString("blood_type")
                telno.text = testVal.getString("telno")
            } catch (e: Exception) {
                e.printStackTrace()
                // 예외 처리 필요
            }
        }

        ProtectorService.getProtectorOfUser(requireContext(), 1) { res ->
            try {
                for (i in 0 until res.length()) {
                    val testVal = res.getJSONObject(i)

                    // nlayout의 복사본을 만들고 UI 요소에 데이터 설정
                    val newLinearLayout = LayoutInflater.from(requireContext()).inflate(R.layout.protector_layout, null) as LinearLayout
                    val name = newLinearLayout.findViewById<TextView>(R.id.protectorName)
                    val telno = newLinearLayout.findViewById<TextView>(R.id.protectorTelno)

                    val builder = AlertDialog.Builder(requireContext())

                    name.text = "[${testVal.getString("relationship")}] ${testVal.getString("name")}"
                    telno.text = testVal.getString("telno")

                    name.tag = "@+id/${name.id}_${i}"
                    telno.tag = "@+id/${telno.id}_${i}"

                    val index = i  // 클로저 내에서 i의 복사본을 사용

                    newLinearLayout.findViewById<Button>(R.id.modifyProtector).setOnClickListener {
                        val intent = Intent(activity, GuardianModify::class.java)
                        startActivity(intent)
                    }

                    newLinearLayout.findViewById<Button>(R.id.deleteProtector).setOnClickListener {
                        builder.setTitle("Alert")
                            .setMessage("정말로 삭제 하시겠습니까?")
                            .setPositiveButton("네") { dialog, _ ->
                                ProtectorService.deleteDelete(this, index) { res ->
                                    dialog.dismiss()

                                    builder.setTitle("Alert")
                                        .setMessage("삭제가 완료되었습니다.")
                                        .setPositiveButton("확인") { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                }
                            }
                            .setNegativeButton("아니요") { dialog, _ ->
                                dialog.dismiss()
                            }
                    }

                    protectorLayout?.addView(newLinearLayout)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // 예외 처리 필요
            }
        }

        modifyBtn.setOnClickListener {
            val intent = Intent(activity, UserModify::class.java)
            startActivity(intent)
        }

        addProtectorBtn.setOnClickListener {
            val intent = Intent(activity, GuardianAdd::class.java)
            startActivity(intent)
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
}
