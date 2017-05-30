package br.com.madoc.rssreader.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.madoc.rssreader.R;
import br.com.madoc.rssreader.fragment.FeedRSSFragment;
import br.com.madoc.rssreader.model.RSSMenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.fab)
    protected FloatingActionButton addFeedButton;

    @BindView(R.id.drawer_layout)
    protected DrawerLayout drawer;

    @BindView(R.id.nav_view)
    protected NavigationView navigationView;

    @BindView(R.id.withoutFeedMessage)
    protected TextView withoutFeedMessageView;

    private RSSMenuItem currentRss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        addFeedButton.setOnClickListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        setupMenu();

        initialize();
    }

    public void initialize() {
        getSupportActionBar().setTitle(R.string.app_name);
        List<RSSMenuItem> menuItems = RSSMenuItem.listAll(RSSMenuItem.class);
        if (menuItems != null && !menuItems.isEmpty()) {
            changeContent(menuItems.get(0));
        } else {
            withoutFeedMessageView.setVisibility(View.VISIBLE);
        }
    }

    public void setFragment(Fragment fragment, boolean root) {
        FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, fragment);
        if (!root) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        RSSMenuItem rssMenuItem = RSSMenuItem.findById(RSSMenuItem.class, id);
        if (rssMenuItem != null) {
            changeContent(rssMenuItem);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                setupInputDialog();
        }
    }

    private void setupInputDialog() {
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(this);

        View promptAddRss = LayoutInflater.from(this).inflate(R.layout.prompt_add_rss, null);

        final EditText titleRss = (EditText) promptAddRss.findViewById(R.id.titleEditText);
        final EditText urlRss = (EditText) promptAddRss.findViewById(R.id.urlEditText);

        inputDialog.setView(promptAddRss);
        inputDialog.setTitle(R.string.prompt_add_rss_title);
        inputDialog.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!titleRss.getText().toString().isEmpty() && URLUtil.isValidUrl(urlRss.getText().toString())) {
                    RSSMenuItem rssMenuItem = new RSSMenuItem();
                    rssMenuItem.setTitle(titleRss.getText().toString());
                    rssMenuItem.setUrl(urlRss.getText().toString());
                    rssMenuItem.save();

                    changeContent(rssMenuItem);

                    Snackbar.make(getCurrentFocus(), "Feed " + rssMenuItem.getTitle() + " adicionado com sucesso!", Snackbar.LENGTH_LONG).show();
                    setupMenu();
                } else {
                    Toast.makeText(getApplicationContext(), "Não foi possível adicionar o feed. Você precisa preencher os campos corretamente.", Toast.LENGTH_LONG).show();
                }
            }
        });
        inputDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        inputDialog.show();
    }


    public void setupMenu() {
        navigationView.getMenu().clear();
        List<RSSMenuItem> rssMenuItemList = RSSMenuItem.listAll(RSSMenuItem.class);
        for (RSSMenuItem rssMenuItem : rssMenuItemList) {
            navigationView.getMenu().add(Menu.NONE, rssMenuItem.getId().intValue(), Menu.NONE, rssMenuItem.getTitle());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_feed_action:
                if (currentRss != null) {
                    AlertDialog.Builder confirm = new AlertDialog.Builder(this)
                            .setTitle("Atenção")
                            .setMessage("Você tem certeza que deseja remover o feed '" + currentRss.getTitle() + "'?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    currentRss.delete();
                                    setupMenu();
                                    initialize();
                                    Snackbar.make(getCurrentFocus(), "Feed removido com sucesso!", Snackbar.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    confirm.show();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeContent(RSSMenuItem rssMenuItem) {
        currentRss = rssMenuItem;
        FeedRSSFragment feedRSSFragment = FeedRSSFragment.newInstance(rssMenuItem);
        setFragment(feedRSSFragment, true);
        withoutFeedMessageView.setVisibility(View.GONE);
    }

}
