package com.landsofruin.companion.fragment;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.landsofruin.companion.GameSetupActivity;
import com.landsofruin.companion.cards.CharacterCardFragment;
import com.landsofruin.companion.cards.FriendlyCharacterView;
import com.landsofruin.companion.cards.FriendlySquadView;
import com.landsofruin.companion.cards.events.CardSelectedEvent;
import com.landsofruin.companion.device.PlayerAccount;
import com.landsofruin.companion.eventbus.BusProvider;
import com.landsofruin.companion.eventbus.CharacterSelectedEvent;
import com.landsofruin.companion.net.event.GameStateChangedEvent;
import com.landsofruin.companion.provider.snapshots.CharacterSnapshot;
import com.landsofruin.companion.provider.snapshots.TribeSnapshot;
import com.landsofruin.companion.state.CharacterState;
import com.landsofruin.companion.state.CharacterStateHero;
import com.landsofruin.companion.state.CharacterStateSquad;
import com.landsofruin.companion.state.GameState;
import com.landsofruin.companion.state.PlayerState;
import com.landsofruin.companion.state.TeamState;
import com.landsofruin.companion.state.builder.CharacterStateBuilder;
import com.landsofruin.companion.state.builder.TeamStateBuilder;
import com.landsofruin.companion.state.gameruleobjects.charactertypes.CharacterType;
import com.landsofruin.companion.state.gameruleobjects.skill.Skill;
import com.landsofruin.companion.state.gameruleobjects.wargear.Wargear;
import com.landsofruin.companion.state.gameruleobjects.wargear.WargearOffensive;
import com.landsofruin.companion.state.proxyobjecthelper.LookupHelper;
import com.landsofruin.companion.state.transition.ChangeTeamTransition;
import com.landsofruin.companion.state.transition.PlayerReadySetupTeamTransition;
import com.landsofruin.companion.state.transition.Transition;
import com.landsofruin.companion.tribemanagement.TribeCharacter;
import com.landsofruin.companion.tribemanagement.UserAccount;
import com.landsofruin.companion.user.UserAccountManager;
import com.landsofruin.companion.utils.UIUtils;
import com.landsofruin.gametracker.R;
import com.landsofruin.gametracker.skills.SkillsManager;
import com.landsofruin.gametracker.wargear.WargearCategoryToIconMapper;
import com.landsofruin.gametracker.wargear.WargearManager;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class GameSetupTeamSelectFirebaseFragment extends Fragment {

    private int MIN_COL_WIDTH = 100;//dp

    private TribeAdapter adapter;
    private List<TribeCharacter> characters = new ArrayList<>();
    private List<String> selectedIds = new ArrayList<>();
    private View doneButton;
    private TextView validationText;
    private UserAccount userAccount;
    private TribeSnapshot tribe;
    private RecyclerView tribeRecyclerView;
    private HashMap<String, Integer> squadMemberCounts = new HashMap<>();


    public static GameSetupTeamSelectFirebaseFragment newInstance() {
        GameSetupTeamSelectFirebaseFragment fragment = new GameSetupTeamSelectFirebaseFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
    }


    public List<TribeCharacter> getSelectedCharacter() {
        List<TribeCharacter> ret = new LinkedList<>();

        for (TribeCharacter tribeCharacter : this.characters) {
            if (this.selectedIds.contains(tribeCharacter.getId())) {
                ret.add(tribeCharacter);


                for (String squadid : tribeCharacter.getSquads()) {
                    if (squadMemberCounts.keySet().contains(squadid) && squadMemberCounts.get(squadid) > 0) {

                        for (TribeCharacter tribeSquadCharacter : this.characters) {
                            if (tribeSquadCharacter.getId().equals(squadid)) {
                                tribeSquadCharacter.setHeroIdentifier(tribeCharacter.getId());
                                ret.add(tribeSquadCharacter);
                            }
                        }
                    }
                }

            }
        }

        return ret;
    }


    private void setMemberCountForSquad(String id, int count) {
        squadMemberCounts.put(id, count);
    }

    public HashMap<String, Integer> getSquadMemberCounts() {
        return squadMemberCounts;
    }

    private int getMemberCountForSquad(String id) {
        Integer ret = squadMemberCounts.get(id);
        if (ret == null) {
            return 0;
        }
        return ret;
    }


    @Subscribe
    public void onSeclectDeselectEvent(SelectDeselectEvent event) {

        String characterId = event.getId();

        if (characterId != null) {

            if (!squadMemberCounts.keySet().contains(characterId)) {


                if (selectedIds.contains(characterId)) {
                    selectedIds.remove(characterId);


                    for (TribeCharacter tribeCharacter : this.characters) {
                        for (String squadid : tribeCharacter.getSquads()) {
                            selectedIds.remove(squadid);
                        }
                    }


                } else {
                    selectedIds.add(characterId);
                }
            }
        }


        ArrayList<CharacterSnapshot> snapshots = new ArrayList<>();

        List<TribeCharacter> tribeCharacters = getSelectedCharacter();
        for (TribeCharacter tribeCharacter : tribeCharacters) {
            snapshots.add(CharacterSnapshot.fromTribeCharacter(tribeCharacter));
        }

        TeamState teamState = TeamStateBuilder.buildFrom(tribe.getServerId(), tribe, snapshots, squadMemberCounts);
        teamState.setTribeName(tribe.getName());
        ChangeTeamTransition transition = new ChangeTeamTransition(PlayerAccount.getUniqueIdentifier(), teamState);
        sendToServer(transition);

        this.adapter.setSelectedIds(selectedIds);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_game_setup_select_team_firebase, parent, false);


        validationText = (TextView) view.findViewById(R.id.validation_text);
        doneButton = view.findViewById(R.id.done_button);
        doneButton.setSelected(getGame().getMe().isPreGameReady());

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToServer(new PlayerReadySetupTeamTransition(getGame().getMe().getIdentifier(), !doneButton.isSelected()));
            }
        });

        view.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        tribeRecyclerView = (RecyclerView) view.findViewById(R.id.characters_recycler_view);

        tribeRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), getColumnCount()));
        adapter = new TribeAdapter(getActivity());
        tribeRecyclerView.setAdapter(adapter);

        characters.clear();
        List<TribeCharacter> charactersForAdapter = new ArrayList<>();
        userAccount = UserAccountManager.getInstance().getUserAccount();
        for (TribeCharacter tribeCharacter : userAccount.getTribe().getCharactersValues()) {

            if (LookupHelper.getInstance().getCharacterTypeFor(tribeCharacter.getCharacterType()).getType() == CharacterType.TYPE_HERO) {
                charactersForAdapter.add(tribeCharacter);
            }
            characters.add(tribeCharacter);

        }
        tribe = TribeSnapshot.fromTribe(userAccount.getTribe());

        adapter.setCharacters(charactersForAdapter);

        BusProvider.postOnMainThread(new SelectDeselectEvent(null));
        updateUI();

        TextView scenatioTitleText = (TextView) view.findViewById(R.id.scenario_title);
        TextView mapTitleText = (TextView) view.findViewById(R.id.map_title);
        TextView teamTitleText = (TextView) view.findViewById(R.id.team_title);
        TextView overviewTitleText = (TextView) view.findViewById(R.id.overview_title);
        TextView startGameTitleText = (TextView) view.findViewById(R.id.start_game_title);

        scenatioTitleText.setTextColor(getResources().getColor(R.color.text_light_blue));
        mapTitleText.setTextColor(getResources().getColor(R.color.text_light_blue));
        teamTitleText.setTextColor(getResources().getColor(R.color.text_light_blue));
        overviewTitleText.setTextColor(getResources().getColor(R.color.text_dark_blue));
        startGameTitleText.setTextColor(getResources().getColor(R.color.text_dark_blue));


        return view;
    }


    private int getColumnCount() {
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;


        float availableSpace = UIUtils.convertPixelsToDp(width, getContext()) - 480;
        float minColWidth = UIUtils.convertDpToPixel(MIN_COL_WIDTH, getContext());
        return (int) (availableSpace / minColWidth);


    }

    private GameState getGame() {
        return ((GameSetupActivity) getActivity()).getGame();
    }

    private void sendToServer(Transition transition) {
        ((GameSetupActivity) getActivity()).sendToServer(transition);
    }


    private void updateUI() {


        GameState game = getGame();
        TeamState team = game.getMe() != null ? game.getMe().getTeam() : null;


        if (team == null) {
            return;
        }

        LinkedList<String> seletedIDs = new LinkedList<>();

        for (CharacterState character : team.listAllTypesCharacters()) {

            if (character instanceof CharacterStateHero) {
                seletedIDs.add(character.getIdentifier());
            }


        }
        this.selectedIds.clear();
        this.selectedIds.addAll(seletedIDs);


        BusProvider.getInstance().post(new SelectedTeamChangedEvent(this.selectedIds));

        validateState(game);
        doneButton.setSelected(getGame().getMe().isPreGameReady());
    }


    @Subscribe
    public void onGameStateChanged(GameStateChangedEvent event) {
        if (getActivity().isFinishing()) {
            return; // Ignore further events
        }

        updateUI();
    }

    @Subscribe
    public void onTribeCharacterTappedEvent(TribeCharacterTappedEvent event) {
        if (getActivity().isFinishing()) {
            return;
        }

        getChildFragmentManager().beginTransaction().replace(R.id.selected_character_container, CardContainerFragment.newInstance(event.getCharacter(), selectedIds.contains(event.getCharacter().getId()))).commit();


    }


    @Subscribe
    public void onCardSelectedEvent(CardSelectedEvent event) {

        CharacterCardFragment fragment = event.getSelectedCharacterCardFragment();
        if (fragment != null) {

            if (fragment.getCharacter() != null) {
                BusProvider.getInstance().post(new CharacterSelectedEvent(fragment.getCharacter()));
            }

        }
    }


    private void validateState(GameState game) {

        if (isMyPlayerTeamReady(game)) {
            doneButton.setEnabled(true);

            if (game.getMe().isPreGameReady()) {
                validationText.setText("Waiting for opponent");
            } else {
                validationText.setText("Press ready to confirm your selection");
            }

        } else {
            validationText.setText("Select your team");
            doneButton.setEnabled(false);
        }
    }

    public static class SelectDeselectEvent {
        private String id;

        public SelectDeselectEvent(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public static class SelectedTeamChangedEvent {

        private List<String> selectedIds;

        public SelectedTeamChangedEvent(List<String> selectedIds) {
            this.selectedIds = selectedIds;
        }

        public List<String> getSelectedIds() {
            return selectedIds;
        }
    }


    public boolean isAllPlayerTeamReady(GameState gameState) {
        for (PlayerState player : gameState.getPlayers()) {
            if (player.getTeam() == null ||
                    player.getTeam().listAllTypesCharacters().size() == 0) {
                return false;
            }
        }
        return true;
    }


    public boolean isMyPlayerTeamReady(GameState gameState) {
        PlayerState player = gameState.getMe();
        if (player.getTeam() == null ||
                player.getTeam().listAllTypesCharacters().size() == 0) {
            return false;
        }

        return true;
    }

    public static class TribeAdapter extends RecyclerView.Adapter<TribeAdapter.ViewHolder> {

        private Context context;
        private List<TribeCharacter> characters = new ArrayList<>();
        private List<String> selectedIds;

        public TribeAdapter(Context context) {
            this.context = context;
        }


        public void setSelectedIds(List<String> selectedIds) {
            this.selectedIds = selectedIds;
            notifyDataSetChanged();
        }

        public void setCharacters(List<TribeCharacter> characters) {
            this.characters = characters;
            notifyDataSetChanged();
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.one_character_selector_team, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            final TribeCharacter character = this.characters.get(position);

            holder.characterName.setText(character.getName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusProvider.getInstance().post(new TribeCharacterTappedEvent(character));
                }
            });

            Picasso.with(context).load(character.getPortraitImageUrl()).into(holder.characterImage);


            if (selectedIds != null && selectedIds.contains(character.getId())) {
                holder.itemView.animate().alpha(1);
                holder.inTeamIndicator.animate().alpha(1);
            } else {
                holder.itemView.animate().alpha(0.5f);
                holder.inTeamIndicator.animate().alpha(0);
            }
        }

        @Override
        public int getItemCount() {
            return characters.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {


            private final TextView characterName;
            private final ImageView characterImage;
            private final View inTeamIndicator;

            public ViewHolder(View v) {
                super(v);
                this.characterName = (TextView) v.findViewById(R.id.character_name);
                characterImage = (ImageView) v.findViewById(R.id.character_image);
                inTeamIndicator = v.findViewById(R.id.in_team_indicator);

            }
        }
    }


    public static class TribeCharacterTappedEvent {
        private TribeCharacter character;

        public TribeCharacterTappedEvent(TribeCharacter character) {
            this.character = character;
        }

        public TribeCharacter getCharacter() {
            return character;
        }
    }


    public static class CardContainerFragment extends Fragment {

        private static final String ARGUMENT_PC_ID_1 = "ARGUMENT_PC_ID_1";
        private static final String ARGUMENT_PC_ID_2 = "ARGUMENT_PC_ID_2";
        private TribeCharacter character;
        private UserAccount userAccount;
        private ImageView profilePic;
        private ViewGroup weaponsContainer;
        private ViewGroup gearContainer;
        private ViewGroup skillsContainer;
        private ViewGroup cardContainer;
        private TextView buttonText;
        private DatabaseReference firebaseRef;
        private ViewGroup squadsContainer;
        private Collection<TribeCharacter> allCharacters;
        private HashMap<String, TextView> squadCounts = new HashMap<>();


        public static CardContainerFragment newInstance(TribeCharacter pc1, boolean isInTeam) {
            CardContainerFragment ret = new CardContainerFragment();

            Bundle args = new Bundle();
            args.putString(ARGUMENT_PC_ID_1, pc1.getId());
            args.putBoolean(ARGUMENT_PC_ID_2, isInTeam);
            ret.setArguments(args);

            Log.e("TMP", "pc1.getId() " + pc1.getId());

            return ret;
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            BusProvider.getInstance().register(this);
        }

        @Override
        public void onDetach() {
            super.onDetach();
            BusProvider.getInstance().unregister(this);
        }


        @Subscribe
        public void onSelectedTeamChangedEvent(SelectedTeamChangedEvent selectedTeamChangedEvent) {
            if (selectedTeamChangedEvent.getSelectedIds().contains(this.character.getId())) {
                buttonText.setText("Remove from team");
            } else {
                buttonText.setText("Add to team");
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.game_setup_character, container, false);
            view.setAlpha(0);
            final String characterId = getArguments().getString(ARGUMENT_PC_ID_1);
            final boolean isInTeam = getArguments().getBoolean(ARGUMENT_PC_ID_2);

            cardContainer = (ViewGroup) view.findViewById(R.id.card_container);
            buttonText = (TextView) view.findViewById(R.id.button_text);


            view.findViewById(R.id.add_remove_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (character == null) {
                        return;
                    }


                    BusProvider.getInstance().post(new SelectDeselectEvent(character.getId()));
                }
            });


            squadsContainer = (ViewGroup) view.findViewById(R.id.squads);
            weaponsContainer = (ViewGroup) view.findViewById(R.id.weapons);
            gearContainer = (ViewGroup) view.findViewById(R.id.gear);
            skillsContainer = (ViewGroup) view.findViewById(R.id.skills);

            profilePic = (ImageView) view.findViewById(R.id.profile_pic);


            userAccount = UserAccountManager.getInstance().getUserAccount();


            allCharacters = userAccount.getTribe().getCharactersValues();
            for (TribeCharacter tribeCharacter : userAccount.getTribe().getCharactersValues()) {

                if (tribeCharacter.getId().equals(characterId)) {
                    character = tribeCharacter;
                    break;
                }

            }

            GameState game = ((GameSetupActivity) getActivity()).getGame();
            TeamState team = game.getMe() != null ? game.getMe().getTeam() : null;


            if (team != null) {

                CharacterSnapshot snapshot = CharacterSnapshot.fromTribeCharacter(character);
                CharacterState characterState = CharacterStateBuilder.buildFrom(snapshot, null, ((GameSetupTeamSelectFirebaseFragment) getParentFragment()).getSquadMemberCounts());
                View cardView;
                if (characterState instanceof CharacterStateSquad) {
                    cardView = new FriendlySquadView(getActivity());
                    ((FriendlySquadView) cardView).setCharacter((CharacterStateSquad) characterState);
                    ((FriendlySquadView) cardView).setRemoveEnabledMode(true);
                } else {
                    cardView = new FriendlyCharacterView(getActivity());
                    ((FriendlyCharacterView) cardView).setCharacter((CharacterStateHero) characterState);
                    ((FriendlyCharacterView) cardView).setRemoveEnabledMode(true);

                }
                cardContainer.removeAllViews();
                cardContainer.addView(cardView);

                view.animate().alpha(1);
            } else {
                Log.e("setup", "Team is null. Can't load characters");
            }


            updateUI();


            if (isInTeam) {
                buttonText.setText("Remove from team");
            } else {
                buttonText.setText("Add to team");
            }

            return view;
        }


        private void updateUI() {
            final LayoutInflater inflater = LayoutInflater.from(getActivity());

            //Squads
            squadsContainer.removeAllViews();

            for (final TribeCharacter squad : allCharacters) {

                if (character.getSquads().contains(squad.getId())) {
                    View squadView = inflater.inflate(
                            R.layout.one_squad_character_select_with_counter, squadsContainer,
                            false);

                    Picasso.with(getContext()).load(squad.getPortraitImageUrl()).into((ImageView) squadView.findViewById(R.id.image));


                    squadCounts.put(squad.getId(), ((TextView) squadView.findViewById(R.id.size)));


                    squadView.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int count = ((GameSetupTeamSelectFirebaseFragment) getParentFragment()).getMemberCountForSquad(squad.getId());
                            if (count + 1 <= 3) {
                                ((GameSetupTeamSelectFirebaseFragment) getParentFragment()).setMemberCountForSquad(squad.getId(), count + 1);
                                updateSquadCounts();

                                BusProvider.getInstance().post(new SelectDeselectEvent(squad.getId()));
                            }

                        }
                    });


                    squadView.findViewById(R.id.remove).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int count = ((GameSetupTeamSelectFirebaseFragment) getParentFragment()).getMemberCountForSquad(squad.getId());

                            if (count - 1 >= 0) {
                                ((GameSetupTeamSelectFirebaseFragment) getParentFragment()).setMemberCountForSquad(squad.getId(), count - 1);
                                updateSquadCounts();

                                BusProvider.getInstance().post(new SelectDeselectEvent(squad.getId()));
                            }
                        }
                    });

                    ((TextView) squadView.findViewById(R.id.name)).setText(squad.getName());
                    squadsContainer.addView(squadView);
                }
            }

            //Weapons
            weaponsContainer.removeAllViews();
            for (Integer weaponId : character.getWeapons()) {
                LinkedList<Wargear> weaponWgs = WargearManager.getInstance().getWargearByWeaponID(weaponId);
                WargearOffensive weapon = (WargearOffensive) weaponWgs.getFirst();
                View weaponView = inflater.inflate(R.layout.one_weapon_character_select, weaponsContainer, false);

                ((TextView) weaponView.findViewById(R.id.name)).setText(weapon.getName());

                if (weapon.getBulletsPerAction() > 0) {
                    weaponView.findViewById(R.id.ammo).setVisibility(View.VISIBLE);
                    ((TextView) weaponView.findViewById(R.id.ammo)).setText(weapon.getClipName() + " x " + character.getAmmoForWeapon(weaponId));
                }

                Integer resource = WargearCategoryToIconMapper.getIconResourceForCategory(weapon.getCategory());
                ((ImageView) weaponView.findViewById(R.id.weapon_icon)).setImageResource(resource);

                ((TextView) weaponView.findViewById(R.id.description)).setText(weapon.getCategory());
                weaponsContainer.addView(weaponView);
            }

            //Gear
            gearContainer.removeAllViews();
            for (Integer wargearId : character.getWargear()) {
                if (wargearId == null) {
                    continue;
                }

                Wargear wargear = WargearManager.getInstance().getWargearById(wargearId);

                View weaponView = inflater.inflate(R.layout.one_weapon_character_select, gearContainer, false);

                ((TextView) weaponView.findViewById(R.id.name)).setText(wargear.getName());

                ((TextView) weaponView.findViewById(R.id.description)).setText(wargear.getCategory());
                gearContainer.addView(weaponView);
            }


            //Skills
            skillsContainer.removeAllViews();
            for (Integer skillId : character.getSkills()) {
                Skill skill = SkillsManager.getInstance().getSkillByID(skillId);

                View weaponView = inflater.inflate(R.layout.one_weapon_character_select, skillsContainer, false);
                ((TextView) weaponView.findViewById(R.id.name)).setText(skill.getName());
                ((TextView) weaponView.findViewById(R.id.description)).setText(skill.getDescription());
                skillsContainer.addView(weaponView);
            }
            updateSquadCounts();
        }


        private void updateSquadCounts() {

            for (String squad : squadCounts.keySet()) {
                int count = ((GameSetupTeamSelectFirebaseFragment) getParentFragment()).getMemberCountForSquad(squad);

                squadCounts.get(squad).setText("" + count);

            }

        }
    }
}

