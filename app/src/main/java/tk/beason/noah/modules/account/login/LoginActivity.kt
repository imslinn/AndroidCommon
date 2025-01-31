/*
 * Copyright (C) 2016 The beasontk Android Source Project
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tk.beason.noah.modules.account.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import tk.beason.common.extensions.animationChild
import tk.beason.common.utils.router.AppRouter
import tk.beason.common.widget.tablayout.TabLayoutPro
import tk.beason.noah.R
import tk.beason.noah.modules.account.password.forgot.ForgotPasswordActivity
import tk.beason.noah.modules.account.register.RegisterActivity
import tk.beason.noah.modules.base.AppBaseActivity
import tk.beason.noah.modules.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*


/**
 * Created by beasontk on 2018/8/8.
 * 登录界面
 */
class LoginActivity : tk.beason.noah.modules.base.AppBaseActivity(),
    tk.beason.noah.modules.account.login.LoginContract.View, View.OnClickListener {

    companion object {
        /**
         * 是不是只关闭
         */
        const val KEY_ONLY_FINISH = "key_login_only_finish"
        /**
         * 是不是去首页
         */
        const val KEY_GOTO_MAIN = "key_go_to_main"
    }

    private lateinit var mPresenter: tk.beason.noah.modules.account.login.LoginContract.Presenter

    private var isOnlyFinish: Boolean = false
    private var isGoToMain: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mPresenter = tk.beason.noah.modules.account.login.LoginPresenter(this)
        mPresenter.init()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        isOnlyFinish = intent.getBooleanExtra(tk.beason.noah.modules.account.login.LoginActivity.Companion.KEY_ONLY_FINISH, false)
        isGoToMain = intent.getBooleanExtra(tk.beason.noah.modules.account.login.LoginActivity.Companion.KEY_GOTO_MAIN, false)
    }

    override fun initNecessaryData() {
        super.initNecessaryData()
        isOnlyFinish = intent.getBooleanExtra(tk.beason.noah.modules.account.login.LoginActivity.Companion.KEY_ONLY_FINISH, false)
        isGoToMain = intent.getBooleanExtra(tk.beason.noah.modules.account.login.LoginActivity.Companion.KEY_GOTO_MAIN, false)
    }

    override fun initViews() {
        super.initViews()
        root?.animationChild()
        var tab = tabLayout.newTab()
        tab.text = getString(R.string.login_password)
        tabLayout.addTab(tab)
        tab = tabLayout.newTab()
        tab.text = getString(R.string.login_by_phone)
        tabLayout.addTab(tab)
        tabLayout.addOnTabSelectedListener(object : TabLayoutPro.OnTabSelectedListener {
            override fun onTabUnselected(tab: TabLayoutPro.Tab?) {
            }

            override fun onTabReselected(tab: TabLayoutPro.Tab?) {
            }

            override fun onTabSelected(tab: TabLayoutPro.Tab?) {
                tab?.let {
                    if (it.position == 0) {
                        password.visibility = View.VISIBLE
                        verifyCode.visibility = View.GONE
                    } else {
                        password.visibility = View.GONE
                        verifyCode.visibility = View.VISIBLE
                    }
                }
            }
        })

        login.setOnClickListener(this)
        register.setOnClickListener(this)
        forgotPassword.setOnClickListener(this)
    }


    override fun onClick(v: View) {
        when (v.id) {

            R.id.login -> {
                //mPresenter.login(mEditAccountView.inputText, mEditPasswordView.inputText)
            }

            R.id.register -> {
                AppRouter.with(this)
                        .target(tk.beason.noah.modules.account.register.RegisterActivity::class.java)!!
                        .start()
            }

            R.id.forgotPassword -> {
                AppRouter.with(this)
                        .target(tk.beason.noah.modules.account.password.forgot.ForgotPasswordActivity::class.java)!!
                        .start()
            }
        }
    }


    override fun updateUI(account: String) {
        //account.setInputText(account)
    }


    override fun showSuccess(message: String) {
        if (!isOnlyFinish) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        finish()
    }


    override fun onBackPressed() {
        if (isGoToMain) {
            AppRouter.with(this@LoginActivity)
                    .target(MainActivity::class.java)
                    .flag(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    .finishSelf()
                    .start()
        } else {
            super.onBackPressed()
        }
    }


}
