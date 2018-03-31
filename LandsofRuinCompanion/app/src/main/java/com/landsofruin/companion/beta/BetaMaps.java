package com.landsofruin.companion.beta;

import com.landsofruin.companion.state.MapState;
import com.landsofruin.companion.state.PointState;
import com.landsofruin.companion.state.RegionState;

import java.util.ArrayList;
import java.util.List;

/**
 * Pre-created maps for the beta release.
 */
public class BetaMaps {


    public static MapState[] createMaps() {
        return new MapState[]{
                createKrakenWarzoneCity(),
                createOffice5(),
                createOffice3(),
                createTall(),
                createGamemat2(),
                createGamemat1(),
                createDroidcon(),
                createFourBlockWithRoads(),
                createSimpleTable(),
                createSimpleWithRoads(),
                createSixtiesMap(),
                createOffice2(),
                createOurTable(),
                createSquareMap(),
                createThreeInARow(),
                createKarolsMap(),
                createWarboardsCityscape3Map(),
                createWarboardsCityscape1Map(),
                createWarboardsCityscape1And3Map(),
                createLoROffice1()
        };
    }

    /**
     * o---o---o---o
     * | A | B | C |
     * o---o---o---o
     * | D | E | F |
     * o---o---o---o
     *
     * @return
     */
    private static MapState createSixtiesMap() {
        MapState map = new MapState();
        map.setName("Basic");
        map.setGroupName("Blank");
        RegionState regionA = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(100, 0),
                new PointState(100, 100),
                new PointState(0, 100)
        });
        regionA.setLabel("A");
        regionA.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionB = new RegionState(new PointState[]{
                new PointState(100, 0),
                new PointState(200, 0),
                new PointState(200, 100),
                new PointState(100, 100)
        });
        regionB.setLabel("B");
        regionB.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionC = new RegionState(new PointState[]{
                new PointState(200, 0),
                new PointState(300, 0),
                new PointState(300, 100),
                new PointState(200, 100)
        });
        regionC.setLabel("C");
        regionC.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionD = new RegionState(new PointState[]{
                new PointState(0, 100),
                new PointState(100, 100),
                new PointState(100, 200),
                new PointState(0, 200)
        });
        regionD.setLabel("D");
        regionD.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionE = new RegionState(new PointState[]{
                new PointState(100, 100),
                new PointState(200, 100),
                new PointState(200, 200),
                new PointState(100, 200)
        });
        regionE.setLabel("E");
        regionE.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionF = new RegionState(new PointState[]{
                new PointState(200, 100),
                new PointState(300, 100),
                new PointState(300, 200),
                new PointState(200, 200)
        });
        regionF.setLabel("F");
        regionF.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        List<RegionState> regions = new ArrayList<>();
        regions.add(regionA);
        regions.add(regionB);
        regions.add(regionC);
        regions.add(regionD);
        regions.add(regionE);
        regions.add(regionF);

        map.setRegionsAndAddSpecials(regions);

        return map;
    }


    /**
     * o---o---o
     * | A | B |
     * o---o---o
     * | D | E |
     * o---o---o
     *
     * @return
     */
    private static MapState createGamemat1() {
        MapState map = new MapState();
        map.setGroupName("Gamemat.eu");
        map.setName("Gamemat.eu 4x4 Wastelands");
        RegionState regionA = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(200, 0),
                new PointState(200, 200),
                new PointState(0, 200)
        });
        regionA.setLabel("A");
        regionA.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionB = new RegionState(new PointState[]{
                new PointState(200, 0),
                new PointState(400, 0),
                new PointState(400, 200),
                new PointState(200, 200)
        });
        regionB.setLabel("B");
        regionB.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);


        RegionState regionD = new RegionState(new PointState[]{
                new PointState(0, 200),
                new PointState(200, 200),
                new PointState(200, 400),
                new PointState(0, 400)
        });
        regionD.setLabel("D");
        regionD.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionE = new RegionState(new PointState[]{
                new PointState(200, 200),
                new PointState(400, 200),
                new PointState(400, 400),
                new PointState(200, 400)
        });
        regionE.setLabel("E");
        regionE.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);


        List<RegionState> regions = new ArrayList<>();
        regions.add(regionA);
        regions.add(regionB);
        regions.add(regionD);
        regions.add(regionE);

        map.setRegionsAndAddSpecials(regions);
        map.setBackgroundImageURL("file:///android_asset/map_assets/gamemat_map1.png");
        map.setProviderName("Gamemat.eu");
        map.setProviderIconURL("file:///android_asset/map_assets/providers/gm.png");
        map.setProviderURL("http://www.gamemat.eu/4x4-g-mats/4x4-g-mat-wastelands-.html");
        return map;
    }


    /**
     * o---o---o
     * | A | B |
     * o---o---o
     * | D | E |
     * o---o---o
     *
     * @return
     */
    private static MapState createOffice2() {
        MapState map = new MapState();
        map.setGroupName("Lands of Ruin Office");
        map.setName("LoR Office - 2");
        RegionState regionA = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(200, 0),
                new PointState(200, 200),
                new PointState(0, 200)
        });
        regionA.setLabel("A");
        regionA.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionB = new RegionState(new PointState[]{
                new PointState(200, 0),
                new PointState(400, 0),
                new PointState(400, 200),
                new PointState(200, 200)
        });
        regionB.setLabel("B");
        regionB.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);


        RegionState regionD = new RegionState(new PointState[]{
                new PointState(0, 200),
                new PointState(200, 200),
                new PointState(200, 400),
                new PointState(0, 400)
        });
        regionD.setLabel("D");
        regionD.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionE = new RegionState(new PointState[]{
                new PointState(200, 200),
                new PointState(400, 200),
                new PointState(400, 400),
                new PointState(200, 400)
        });
        regionE.setLabel("E");
        regionE.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);


        List<RegionState> regions = new ArrayList<>();
        regions.add(regionA);
        regions.add(regionB);
        regions.add(regionD);
        regions.add(regionE);

        map.setRegionsAndAddSpecials(regions);
        map.setBackgroundImageURL("http://landsofruin.com/app_assets/maps/office_map_2.jpg");
        return map;
    }


    /**
     * o---o---o
     * | A | B |
     * o---o---o
     * | D | E |
     * o---o---o
     *
     * @return
     */
    private static MapState createOffice3() {
        MapState map = new MapState();
        map.setName("LoR Office - 3");
        map.setGroupName("Lands of Ruin Office");
        RegionState regionA = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(200, 0),
                new PointState(200, 200),
                new PointState(0, 200)
        });
        regionA.setLabel("A");
        regionA.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionB = new RegionState(new PointState[]{
                new PointState(200, 0),
                new PointState(400, 0),
                new PointState(400, 200),
                new PointState(200, 200)
        });
        regionB.setLabel("B");
        regionB.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);


        RegionState regionD = new RegionState(new PointState[]{
                new PointState(0, 200),
                new PointState(200, 200),
                new PointState(200, 400),
                new PointState(0, 400)
        });
        regionD.setLabel("D");
        regionD.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionE = new RegionState(new PointState[]{
                new PointState(200, 200),
                new PointState(400, 200),
                new PointState(400, 400),
                new PointState(200, 400)
        });
        regionE.setLabel("E");
        regionE.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);


        List<RegionState> regions = new ArrayList<>();
        regions.add(regionA);
        regions.add(regionB);
        regions.add(regionD);
        regions.add(regionE);

        map.setRegionsAndAddSpecials(regions);
        map.setBackgroundImageURL("http://landsofruin.com/app_assets/wasteland_office.jpg");
        return map;
    }


    /**
     * o---o---o
     * | A | B |
     * o---o---o
     * | D | E |
     * o---o---o
     *
     * @return
     */
    private static MapState createOffice5() {
        MapState map = new MapState();
        map.setGroupName("Lands of Ruin Office");
        map.setName("LoR Office - 3");
        RegionState regionA = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(150, 0),
                new PointState(150, 200),
                new PointState(0, 200)
        });
        regionA.setLabel("A");
        regionA.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionB = new RegionState(new PointState[]{
                new PointState(150, 0),
                new PointState(300, 0),
                new PointState(300, 200),
                new PointState(150, 200)
        });
        regionB.setLabel("B");
        regionB.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);


        RegionState regionD = new RegionState(new PointState[]{
                new PointState(0, 200),
                new PointState(150, 200),
                new PointState(150, 400),
                new PointState(0, 400)
        });
        regionD.setLabel("D");
        regionD.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionE = new RegionState(new PointState[]{
                new PointState(150, 200),
                new PointState(300, 200),
                new PointState(300, 400),
                new PointState(150, 400)
        });
        regionE.setLabel("E");
        regionE.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);


        List<RegionState> regions = new ArrayList<>();
        regions.add(regionA);
        regions.add(regionB);
        regions.add(regionD);
        regions.add(regionE);

        map.setRegionsAndAddSpecials(regions);
        map.setBackgroundImageURL("http://landsofruin.com/app_assets/maps/office_map_5.jpg");
        return map;
    }


    /**
     * o--o--o
     * | A| B|
     * o--o--o
     * | D| E|
     * o--o--o
     *
     * @return
     */
    private static MapState createTall() {
        MapState map = new MapState();
        map.setName("City blocks - tall");
        map.setGroupName("Blank");
        RegionState regionA = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(150, 0),
                new PointState(150, 200),
                new PointState(0, 200)
        });
        regionA.setLabel("A");
        regionA.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionB = new RegionState(new PointState[]{
                new PointState(150, 0),
                new PointState(300, 0),
                new PointState(300, 200),
                new PointState(150, 200)
        });
        regionB.setLabel("B");
        regionB.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);


        RegionState regionD = new RegionState(new PointState[]{
                new PointState(0, 200),
                new PointState(150, 200),
                new PointState(150, 400),
                new PointState(0, 400)
        });
        regionD.setLabel("D");
        regionD.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionE = new RegionState(new PointState[]{
                new PointState(150, 200),
                new PointState(300, 200),
                new PointState(300, 400),
                new PointState(150, 400)
        });
        regionE.setLabel("E");
        regionE.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);


        List<RegionState> regions = new ArrayList<>();
        regions.add(regionA);
        regions.add(regionB);
        regions.add(regionD);
        regions.add(regionE);

        map.setRegionsAndAddSpecials(regions);
        return map;
    }


    /**
     * o---o---o
     * | A | B |
     * o---o---o
     * | D | E |
     * o---o---o
     *
     * @return
     */
    private static MapState createGamemat2() {
        MapState map = new MapState();
        map.setGroupName("Gamemat.eu");
        map.setName("Gamemat.eu 4x4 TriCity");
        RegionState regionA = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(200, 0),
                new PointState(200, 200),
                new PointState(0, 200)
        });
        regionA.setLabel("A");
        regionA.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionB = new RegionState(new PointState[]{
                new PointState(200, 0),
                new PointState(400, 0),
                new PointState(400, 200),
                new PointState(200, 200)
        });
        regionB.setLabel("B");
        regionB.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);


        RegionState regionD = new RegionState(new PointState[]{
                new PointState(0, 200),
                new PointState(200, 200),
                new PointState(200, 400),
                new PointState(0, 400)
        });
        regionD.setLabel("D");
        regionD.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionE = new RegionState(new PointState[]{
                new PointState(200, 200),
                new PointState(400, 200),
                new PointState(400, 400),
                new PointState(200, 400)
        });
        regionE.setLabel("E");
        regionE.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);


        List<RegionState> regions = new ArrayList<>();
        regions.add(regionA);
        regions.add(regionB);
        regions.add(regionD);
        regions.add(regionE);

        map.setRegionsAndAddSpecials(regions);
        map.setBackgroundImageURL("file:///android_asset/map_assets/gamemat_map_2.png");
        map.setProviderName("Gamemat.eu");
        map.setProviderIconURL("file:///android_asset/map_assets/providers/gm.png");
        map.setProviderURL("http://www.gamemat.eu/4x4-g-mats/4x4-g-mat-tricity-.html");
        return map;
    }

    /**
     * o---o---o---o
     * | A | B | C |
     * | o-o---o-o |
     * | |       | |
     * o-o   D   o-o
     * | |       | |
     * | o-o---o-o |
     * | E | F | G |
     * o---o---o---o
     *
     * @return
     */
    private static MapState createSquareMap() {
        MapState map = new MapState();
        map.setName("Town Square");
        map.setGroupName("Blank");
        RegionState regionA = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(100, 0),
                new PointState(100, 50),
                new PointState(50, 50),
                new PointState(50, 100),
                new PointState(0, 100)
        });
        regionA.setLabel("A");
        regionA.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionB = new RegionState(new PointState[]{
                new PointState(100, 0),
                new PointState(200, 0),
                new PointState(200, 50),
                new PointState(100, 50)
        });
        regionB.setLabel("B");
        regionB.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionC = new RegionState(new PointState[]{
                new PointState(200, 0),
                new PointState(300, 0),
                new PointState(300, 100),
                new PointState(250, 100),
                new PointState(250, 50),
                new PointState(200, 50)
        });
        regionC.setLabel("C");
        regionC.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionD = new RegionState(new PointState[]{
                new PointState(50, 50),
                new PointState(250, 50),
                new PointState(250, 150),
                new PointState(50, 150)
        });
        regionD.setLabel("D");
        regionD.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        RegionState regionE = new RegionState(new PointState[]{
                new PointState(0, 100),
                new PointState(50, 100),
                new PointState(50, 150),
                new PointState(100, 150),
                new PointState(100, 200),
                new PointState(0, 200)
        });
        regionE.setLabel("E");
        regionE.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionF = new RegionState(new PointState[]{
                new PointState(100, 150),
                new PointState(200, 150),
                new PointState(200, 200),
                new PointState(100, 200)
        });
        regionF.setLabel("F");
        regionF.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionG = new RegionState(new PointState[]{
                new PointState(250, 100),
                new PointState(300, 100),
                new PointState(300, 200),
                new PointState(200, 200),
                new PointState(200, 150),
                new PointState(250, 150)
        });
        regionG.setLabel("G");
        regionG.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        List<RegionState> regions = new ArrayList<RegionState>();
        regions.add(regionA);
        regions.add(regionB);
        regions.add(regionC);
        regions.add(regionD);
        regions.add(regionE);
        regions.add(regionF);
        regions.add(regionG);

        map.setRegionsAndAddSpecials(regions);

        return map;
    }


    /**
     * o-----o-----o
     * |  A  |  B  |
     * | o---o---o |
     * | |       | |
     * o-o   E   o-o
     * | |       | |
     * | o---o---o |
     * |  C  |  D  |
     * o-----o-----o
     *
     * @return
     */
    private static MapState createKarolsMap() {
        MapState map = new MapState();
        map.setName("Town Square - Small");
        map.setGroupName("Blank");
        RegionState regionA = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(150, 0),
                new PointState(150, 100),
                new PointState(100, 100),
                new PointState(100, 150),
                new PointState(0, 150)
        });
        regionA.setLabel("A");
        regionA.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);


        RegionState regionB = new RegionState(new PointState[]{
                new PointState(150, 0),
                new PointState(300, 0),
                new PointState(300, 150),
                new PointState(200, 150),
                new PointState(200, 100),
                new PointState(150, 100)
        });
        regionB.setLabel("B");
        regionB.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);


        RegionState regionE = new RegionState(new PointState[]{
                new PointState(100, 100),
                new PointState(200, 100),
                new PointState(200, 200),
                new PointState(100, 200)
        });
        regionE.setLabel("E");
        regionE.setRegionType(MapState.MAP_REGION_TYPE_STREET);


        RegionState regionC = new RegionState(new PointState[]{
                new PointState(0, 150),
                new PointState(100, 150),
                new PointState(100, 200),
                new PointState(150, 200),
                new PointState(150, 300),
                new PointState(0, 300)
        });
        regionC.setLabel("C");
        regionC.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);


        RegionState regionD = new RegionState(new PointState[]{
                new PointState(200, 150),
                new PointState(300, 150),
                new PointState(300, 300),
                new PointState(150, 300),
                new PointState(150, 200),
                new PointState(200, 200)
        });
        regionD.setLabel("D");
        regionD.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        List<RegionState> regions = new ArrayList<RegionState>();
        regions.add(regionA);
        regions.add(regionB);
        regions.add(regionC);
        regions.add(regionD);
        regions.add(regionE);

        map.setRegionsAndAddSpecials(regions);

        return map;
    }


    /**
     * Create Map for our table
     *
     * @return
     */
    private static MapState createOurTable() {
        MapState map = new MapState();
        map.setName("City Blocks - Large");
        map.setGroupName("Blank");
        RegionState regionA = new RegionState(new PointState[]{
                new PointState(60, 0),
                new PointState(220, 0),
                new PointState(220, 160),
                new PointState(60, 160)
        });
        regionA.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);
        regionA.setLabel("A");

        RegionState regionB = new RegionState(new PointState[]{
                new PointState(280, 0),
                new PointState(440, 0),
                new PointState(440, 160),
                new PointState(280, 160)
        });
        regionB.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);
        regionB.setLabel("B");

        RegionState regionC = new RegionState(new PointState[]{
                new PointState(500, 0),
                new PointState(660, 0),
                new PointState(660, 160),
                new PointState(500, 160)
        });
        regionC.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);
        regionC.setLabel("C");

        RegionState regionD = new RegionState(new PointState[]{
                new PointState(60, 220),
                new PointState(220, 220),
                new PointState(220, 380),
                new PointState(60, 380)
        });
        regionD.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);
        regionD.setLabel("D");

        RegionState regionE = new RegionState(new PointState[]{
                new PointState(280, 220),
                new PointState(440, 220),
                new PointState(440, 380),
                new PointState(280, 380)
        });
        regionE.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);
        regionE.setLabel("E");

        RegionState regionF = new RegionState(new PointState[]{
                new PointState(500, 220),
                new PointState(660, 220),
                new PointState(660, 380),
                new PointState(500, 380)
        });
        regionF.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);
        regionF.setLabel("F");

        RegionState regionG = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(60, 0),
                new PointState(60, 380),
                new PointState(0, 380)
        });
        regionG.setRegionType(MapState.MAP_REGION_TYPE_STREET);
        regionG.setLabel("G");

        RegionState regionH = new RegionState(new PointState[]{
                new PointState(220, 0),
                new PointState(280, 0),
                new PointState(280, 380),
                new PointState(220, 380)
        });
        regionH.setRegionType(MapState.MAP_REGION_TYPE_STREET);
        regionH.setLabel("H");

        RegionState regionI = new RegionState(new PointState[]{
                new PointState(440, 0),
                new PointState(500, 0),
                new PointState(500, 380),
                new PointState(440, 380)
        });
        regionI.setRegionType(MapState.MAP_REGION_TYPE_STREET);
        regionI.setLabel("I");

        RegionState regionJ = new RegionState(new PointState[]{
                new PointState(660, 0),
                new PointState(720, 0),
                new PointState(720, 380),
                new PointState(660, 380)
        });
        regionJ.setRegionType(MapState.MAP_REGION_TYPE_STREET);
        regionJ.setLabel("J");

        RegionState regionK = new RegionState(new PointState[]{
                new PointState(0, 160),
                new PointState(720, 160),
                new PointState(720, 220),
                new PointState(0, 220)
        });
        regionK.setRegionType(MapState.MAP_REGION_TYPE_STREET);
        regionK.setLabel("K");

        List<RegionState> regions = new ArrayList<RegionState>();
        regions.add(regionA);
        regions.add(regionB);
        regions.add(regionC);
        regions.add(regionD);
        regions.add(regionE);
        regions.add(regionF);
        regions.add(regionG);
        regions.add(regionH);
        regions.add(regionI);
        regions.add(regionJ);
        regions.add(regionK);

        map.setRegionsAndAddSpecials(regions);

        return map;
    }

    private static MapState createSimpleTable() {

        RegionState regionA = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(150, 0),
                new PointState(150, 150),
                new PointState(0, 150)
        });
        regionA.setLabel("A");
        regionA.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionB = new RegionState(new PointState[]{
                new PointState(210, 0),
                new PointState(360, 0),
                new PointState(360, 150),
                new PointState(210, 150)
        });
        regionB.setLabel("B");
        regionB.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionC = new RegionState(new PointState[]{
                new PointState(0, 210),
                new PointState(150, 210),
                new PointState(150, 360),
                new PointState(0, 360)
        });
        regionC.setLabel("C");
        regionC.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionD = new RegionState(new PointState[]{
                new PointState(210, 210),
                new PointState(360, 210),
                new PointState(360, 360),
                new PointState(210, 360)
        });
        regionD.setLabel("D");
        regionD.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionE = new RegionState(new PointState[]{
                new PointState(150, 0),
                new PointState(210, 0),
                new PointState(210, 360),
                new PointState(150, 360)
        });
        regionE.setLabel("E");
        regionE.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        RegionState regionF = new RegionState(new PointState[]{
                new PointState(0, 150),
                new PointState(360, 150),
                new PointState(360, 210),
                new PointState(0, 210)
        });
        regionF.setLabel("F");
        regionF.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        List<RegionState> regions = new ArrayList<RegionState>();
        regions.add(regionA);
        regions.add(regionB);
        regions.add(regionC);
        regions.add(regionD);
        regions.add(regionE);
        regions.add(regionF);

        MapState map = new MapState();
        map.setRegionsAndAddSpecials(regions);
        map.setName("City Blocks - Small");
        map.setGroupName("Blank");
        return map;
    }

    private static MapState createSimpleWithRoads() {
        RegionState regionA = new RegionState(new PointState[]{
                new PointState(60, 60),
                new PointState(210, 60),
                new PointState(210, 210),
                new PointState(60, 210)
        });
        regionA.setLabel("A");
        regionA.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionB = new RegionState(new PointState[]{
                new PointState(270, 60),
                new PointState(420, 60),
                new PointState(420, 210),
                new PointState(270, 210)
        });
        regionB.setLabel("B");
        regionB.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionC = new RegionState(new PointState[]{
                new PointState(60, 270),
                new PointState(210, 270),
                new PointState(210, 420),
                new PointState(60, 420)
        });
        regionC.setLabel("C");
        regionC.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionD = new RegionState(new PointState[]{
                new PointState(270, 270),
                new PointState(420, 270),
                new PointState(420, 420),
                new PointState(270, 420)
        });
        regionD.setLabel("D");
        regionD.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionE = new RegionState(new PointState[]{
                new PointState(210, 0),
                new PointState(270, 0),
                new PointState(270, 480),
                new PointState(210, 480)
        });
        regionE.setLabel("E");
        regionE.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        RegionState regionF = new RegionState(new PointState[]{
                new PointState(0, 210),
                new PointState(480, 210),
                new PointState(480, 270),
                new PointState(0, 270)
        });
        regionF.setLabel("F");
        regionF.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        RegionState regionG = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(480, 0),
                new PointState(480, 60),
                new PointState(0, 60)
        });
        regionG.setLabel("G");
        regionG.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        RegionState regionH = new RegionState(new PointState[]{
                new PointState(0, 420),
                new PointState(480, 420),
                new PointState(480, 480),
                new PointState(0, 480)
        });
        regionH.setLabel("H");
        regionH.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        RegionState regionI = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(60, 0),
                new PointState(60, 480),
                new PointState(0, 480)
        });
        regionI.setLabel("I");
        regionI.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        RegionState regionJ = new RegionState(new PointState[]{
                new PointState(420, 0),
                new PointState(480, 0),
                new PointState(480, 480),
                new PointState(420, 480)
        });
        regionJ.setLabel("J");
        regionJ.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        List<RegionState> regions = new ArrayList<RegionState>();
        regions.add(regionA);
        regions.add(regionB);
        regions.add(regionC);
        regions.add(regionD);
        regions.add(regionE);
        regions.add(regionF);
        regions.add(regionG);
        regions.add(regionH);
        regions.add(regionI);
        regions.add(regionJ);

        MapState map = new MapState();
        map.setRegionsAndAddSpecials(regions);

        map.setName("City Blocks  - Small 3");
        map.setGroupName("Blank");
        return map;
    }


    private static MapState createLoROffice1() {
        RegionState regionA = new RegionState(new PointState[]{
                new PointState(60, 60),
                new PointState(240, 60),
                new PointState(240, 240),
                new PointState(60, 240)
        });
        regionA.setLabel("A");
        regionA.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionB = new RegionState(new PointState[]{
                new PointState(240, 60),
                new PointState(420, 60),
                new PointState(420, 240),
                new PointState(240, 240)
        });
        regionB.setLabel("B");
        regionB.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionC = new RegionState(new PointState[]{
                new PointState(60, 240),
                new PointState(240, 240),
                new PointState(240, 420),
                new PointState(60, 420)
        });
        regionC.setLabel("C");
        regionC.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionD = new RegionState(new PointState[]{
                new PointState(240, 240),
                new PointState(420, 240),
                new PointState(420, 420),
                new PointState(240, 420)
        });
        regionD.setLabel("D");
        regionD.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);


        RegionState regionG = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(480, 0),
                new PointState(480, 60),
                new PointState(0, 60)
        });
        regionG.setLabel("G");
        regionG.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        RegionState regionH = new RegionState(new PointState[]{
                new PointState(0, 420),
                new PointState(480, 420),
                new PointState(480, 480),
                new PointState(0, 480)
        });
        regionH.setLabel("H");
        regionH.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        RegionState regionI = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(60, 0),
                new PointState(60, 480),
                new PointState(0, 480)
        });
        regionI.setLabel("I");
        regionI.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        RegionState regionJ = new RegionState(new PointState[]{
                new PointState(420, 0),
                new PointState(480, 0),
                new PointState(480, 480),
                new PointState(420, 480)
        });
        regionJ.setLabel("J");
        regionJ.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        List<RegionState> regions = new ArrayList<RegionState>();
        regions.add(regionA);
        regions.add(regionB);
        regions.add(regionC);
        regions.add(regionD);
        regions.add(regionG);
        regions.add(regionH);
        regions.add(regionI);
        regions.add(regionJ);

        MapState map = new MapState();
        map.setRegionsAndAddSpecials(regions);
        map.setBackgroundImageURL("file:///android_asset/map_assets/lor_map_1.jpg");

        map.setGroupName("Lands of Ruin Office");
        map.setName("Lands of Ruin Office 1");

        return map;
    }


    private static MapState createDroidcon() {
        RegionState regionA = new RegionState(new PointState[]{
                new PointState(60, 60),
                new PointState(210, 60),
                new PointState(210, 210),
                new PointState(60, 210)
        });
        regionA.setLabel("A");
        regionA.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionB = new RegionState(new PointState[]{
                new PointState(270, 60),
                new PointState(420, 60),
                new PointState(420, 210),
                new PointState(270, 210)
        });
        regionB.setLabel("B");
        regionB.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionC = new RegionState(new PointState[]{
                new PointState(60, 270),
                new PointState(210, 270),
                new PointState(210, 420),
                new PointState(60, 420)
        });
        regionC.setLabel("C");
        regionC.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionD = new RegionState(new PointState[]{
                new PointState(270, 270),
                new PointState(420, 270),
                new PointState(420, 420),
                new PointState(270, 420)
        });
        regionD.setLabel("D");
        regionD.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionE = new RegionState(new PointState[]{
                new PointState(210, 0),
                new PointState(270, 0),
                new PointState(270, 480),
                new PointState(210, 480)
        });
        regionE.setLabel("E");
        regionE.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        RegionState regionF = new RegionState(new PointState[]{
                new PointState(0, 210),
                new PointState(480, 210),
                new PointState(480, 270),
                new PointState(0, 270)
        });
        regionF.setLabel("F");
        regionF.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        RegionState regionG = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(480, 0),
                new PointState(480, 60),
                new PointState(0, 60)
        });
        regionG.setLabel("G");
        regionG.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        RegionState regionH = new RegionState(new PointState[]{
                new PointState(0, 420),
                new PointState(480, 420),
                new PointState(480, 480),
                new PointState(0, 480)
        });
        regionH.setLabel("H");
        regionH.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        RegionState regionI = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(60, 0),
                new PointState(60, 480),
                new PointState(0, 480)
        });
        regionI.setLabel("I");
        regionI.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        RegionState regionJ = new RegionState(new PointState[]{
                new PointState(420, 0),
                new PointState(480, 0),
                new PointState(480, 480),
                new PointState(420, 480)
        });
        regionJ.setLabel("J");
        regionJ.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        List<RegionState> regions = new ArrayList<RegionState>();
        regions.add(regionA);
        regions.add(regionB);
        regions.add(regionC);
        regions.add(regionD);
        regions.add(regionE);
        regions.add(regionF);
        regions.add(regionG);
        regions.add(regionH);
        regions.add(regionI);
        regions.add(regionJ);

        MapState map = new MapState();
        map.setRegionsAndAddSpecials(regions);

        map.setName("Droidcon");
        map.setGroupName("Lands of Ruin Office");
        map.setBackgroundImageURL("file:///android_asset/map_assets/droidcon.jpg");


        return map;
    }


    private static MapState createFourBlockWithRoads() {
        RegionState regionA = new RegionState(new PointState[]{
                new PointState(60, 60),
                new PointState(240, 60),
                new PointState(240, 240),
                new PointState(60, 240)
        });
        regionA.setLabel("A");
        regionA.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionB = new RegionState(new PointState[]{
                new PointState(240, 60),
                new PointState(420, 60),
                new PointState(420, 240),
                new PointState(240, 240)
        });
        regionB.setLabel("B");
        regionB.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionC = new RegionState(new PointState[]{
                new PointState(60, 240),
                new PointState(240, 240),
                new PointState(240, 420),
                new PointState(60, 420)
        });
        regionC.setLabel("C");
        regionC.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionD = new RegionState(new PointState[]{
                new PointState(240, 240),
                new PointState(420, 240),
                new PointState(420, 420),
                new PointState(240, 420)
        });
        regionD.setLabel("D");
        regionD.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);


        RegionState regionG = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(480, 0),
                new PointState(480, 60),
                new PointState(0, 60)
        });
        regionG.setLabel("G");
        regionG.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        RegionState regionH = new RegionState(new PointState[]{
                new PointState(0, 420),
                new PointState(480, 420),
                new PointState(480, 480),
                new PointState(0, 480)
        });
        regionH.setLabel("H");
        regionH.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        RegionState regionI = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(60, 0),
                new PointState(60, 480),
                new PointState(0, 480)
        });
        regionI.setLabel("I");
        regionI.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        RegionState regionJ = new RegionState(new PointState[]{
                new PointState(420, 0),
                new PointState(480, 0),
                new PointState(480, 480),
                new PointState(420, 480)
        });
        regionJ.setLabel("J");
        regionJ.setRegionType(MapState.MAP_REGION_TYPE_STREET);

        List<RegionState> regions = new ArrayList<RegionState>();
        regions.add(regionA);
        regions.add(regionB);
        regions.add(regionC);
        regions.add(regionD);
        regions.add(regionG);
        regions.add(regionH);
        regions.add(regionI);
        regions.add(regionJ);

        MapState map = new MapState();
        map.setRegionsAndAddSpecials(regions);

        map.setName("City Blocks - Small 2");
        map.setGroupName("Blank");
        return map;
    }

    private static MapState createThreeInARow() {
        MapState map = new MapState();
        map.setGroupName("Blank");
        RegionState regionA = new RegionState(new PointState[]{
                new PointState(40, 40),
                new PointState(140, 40),
                new PointState(140, 140),
                new PointState(40, 140)
        });
        regionA.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);
        regionA.setLabel("A");

        RegionState regionB = new RegionState(new PointState[]{
                new PointState(180, 40),
                new PointState(280, 40),
                new PointState(280, 140),
                new PointState(180, 140)
        });
        regionB.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);
        regionB.setLabel("B");

        RegionState regionC = new RegionState(new PointState[]{
                new PointState(320, 40),
                new PointState(420, 40),
                new PointState(420, 140),
                new PointState(320, 140)
        });
        regionC.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);
        regionC.setLabel("C");

        RegionState regionD = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(460, 0),
                new PointState(460, 40),
                new PointState(0, 40)
        });
        regionD.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);
        regionD.setLabel("D");

        RegionState regionE = new RegionState(new PointState[]{
                new PointState(0, 140),
                new PointState(460, 140),
                new PointState(460, 180),
                new PointState(0, 180)
        });
        regionE.setRegionType(MapState.MAP_REGION_TYPE_STREET);
        regionE.setLabel("E");

        RegionState regionF = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(40, 0),
                new PointState(40, 180),
                new PointState(0, 180)
        });
        regionF.setRegionType(MapState.MAP_REGION_TYPE_STREET);
        regionF.setLabel("F");

        RegionState regionG = new RegionState(new PointState[]{
                new PointState(140, 0),
                new PointState(180, 0),
                new PointState(180, 180),
                new PointState(140, 180)
        });
        regionG.setRegionType(MapState.MAP_REGION_TYPE_STREET);
        regionG.setLabel("G");

        RegionState regionH = new RegionState(new PointState[]{
                new PointState(280, 0),
                new PointState(320, 0),
                new PointState(320, 180),
                new PointState(280, 180)
        });
        regionH.setRegionType(MapState.MAP_REGION_TYPE_STREET);
        regionH.setLabel("H");

        RegionState regionI = new RegionState(new PointState[]{
                new PointState(420, 0),
                new PointState(460, 0),
                new PointState(460, 180),
                new PointState(420, 180)
        });
        regionI.setRegionType(MapState.MAP_REGION_TYPE_STREET);
        regionI.setLabel("I");

        List<RegionState> regions = new ArrayList<RegionState>();
        regions.add(regionA);
        regions.add(regionB);
        regions.add(regionC);
        regions.add(regionD);
        regions.add(regionE);
        regions.add(regionF);
        regions.add(regionG);
        regions.add(regionH);
        regions.add(regionI);

        map.setRegionsAndAddSpecials(regions);

        map.setName("City Blocks - Row");
        map.setGroupName("Blank");
        return map;
    }


    /**
     * o---o---o---o
     * | A | B | C |
     * o---o---o---o
     * | D | E | F |
     * o---o---o---o
     * <p/>
     * Warboards cityscape 3
     *
     * @return
     */
    private static MapState createWarboardsCityscape3Map() {
        MapState map = new MapState();
        map.setName("Warboards - Cityscape 3");
        map.setGroupName("Warboards");
        RegionState regionA = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(200, 0),
                new PointState(200, 200),
                new PointState(0, 200)
        });
        regionA.setLabel("A");
        regionA.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionB = new RegionState(new PointState[]{
                new PointState(200, 0),
                new PointState(400, 0),
                new PointState(400, 200),
                new PointState(200, 200)
        });
        regionB.setLabel("B");
        regionB.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionC = new RegionState(new PointState[]{
                new PointState(400, 0),
                new PointState(600, 0),
                new PointState(600, 200),
                new PointState(400, 200)
        });
        regionC.setLabel("C");
        regionC.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionD = new RegionState(new PointState[]{
                new PointState(0, 200),
                new PointState(200, 200),
                new PointState(200, 400),
                new PointState(0, 400)
        });
        regionD.setLabel("D");
        regionD.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionE = new RegionState(new PointState[]{
                new PointState(200, 200),
                new PointState(400, 200),
                new PointState(400, 400),
                new PointState(200, 400)
        });
        regionE.setLabel("E");
        regionE.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionF = new RegionState(new PointState[]{
                new PointState(400, 200),
                new PointState(600, 200),
                new PointState(600, 400),
                new PointState(400, 400)
        });
        regionF.setLabel("F");
        regionF.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        List<RegionState> regions = new ArrayList<RegionState>();
        regions.add(regionA);
        regions.add(regionB);
        regions.add(regionC);
        regions.add(regionD);
        regions.add(regionE);
        regions.add(regionF);

        map.setRegionsAndAddSpecials(regions);
        map.setBackgroundImageURL("file:///android_asset/map_assets/warboards_cityscape_3.jpg");
        map.setProviderName("Warsmith");
        map.setProviderURL("https://warsmithpress.wordpress.com/");

        return map;
    }


    /**
     * o---o---o---o
     * | A | B | C |
     * o---o---o---o
     * | D | E | F |
     * o---o---o---o
     * <p/>
     * Warboards cityscape 1
     *
     * @return
     */
    private static MapState createWarboardsCityscape1Map() {
        MapState map = new MapState();
        map.setName("Warboards - Cityscape 1");
        map.setGroupName("Warboards");
        RegionState regionA = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(200, 0),
                new PointState(200, 200),
                new PointState(0, 200)
        });
        regionA.setLabel("A");
        regionA.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionB = new RegionState(new PointState[]{
                new PointState(200, 0),
                new PointState(400, 0),
                new PointState(400, 200),
                new PointState(200, 200)
        });
        regionB.setLabel("B");
        regionB.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionC = new RegionState(new PointState[]{
                new PointState(400, 0),
                new PointState(600, 0),
                new PointState(600, 200),
                new PointState(400, 200)
        });
        regionC.setLabel("C");
        regionC.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionD = new RegionState(new PointState[]{
                new PointState(0, 200),
                new PointState(200, 200),
                new PointState(200, 400),
                new PointState(0, 400)
        });
        regionD.setLabel("D");
        regionD.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionE = new RegionState(new PointState[]{
                new PointState(200, 200),
                new PointState(400, 200),
                new PointState(400, 400),
                new PointState(200, 400)
        });
        regionE.setLabel("E");
        regionE.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionF = new RegionState(new PointState[]{
                new PointState(400, 200),
                new PointState(600, 200),
                new PointState(600, 400),
                new PointState(400, 400)
        });
        regionF.setLabel("F");
        regionF.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        List<RegionState> regions = new ArrayList<RegionState>();
        regions.add(regionA);
        regions.add(regionB);
        regions.add(regionC);
        regions.add(regionD);
        regions.add(regionE);
        regions.add(regionF);

        map.setRegionsAndAddSpecials(regions);
        map.setBackgroundImageURL("file:///android_asset/map_assets/warboards_cityscape_1.jpg");
        map.setProviderURL("https://warsmithpress.wordpress.com/");
        map.setProviderName("Warsmith");
        return map;
    }


    /**
     * o---o---o---o
     * | A | B | C |
     * o---o---o---o
     * | D | E | F |
     * o---o---o---o
     * <p/>
     * Warboards cityscape 1
     *
     * @return
     */
    private static MapState createWarboardsCityscape1And3Map() {
        MapState map = new MapState();
        map.setName("Warboards - Cityscape 1 and 3");
        map.setGroupName("Warboards");
        RegionState regionA = new RegionState(new PointState[]{
                new PointState(0, 0),
                new PointState(200, 0),
                new PointState(200, 250),
                new PointState(0, 250)
        });
        regionA.setLabel("A");
        regionA.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionB = new RegionState(new PointState[]{
                new PointState(200, 0),
                new PointState(400, 0),
                new PointState(400, 250),
                new PointState(200, 250)
        });
        regionB.setLabel("B");
        regionB.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionC = new RegionState(new PointState[]{
                new PointState(400, 0),
                new PointState(600, 0),
                new PointState(600, 250),
                new PointState(400, 250)
        });
        regionC.setLabel("C");
        regionC.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionD = new RegionState(new PointState[]{
                new PointState(0, 250),
                new PointState(200, 250),
                new PointState(200, 500),
                new PointState(0, 500)
        });
        regionD.setLabel("D");
        regionD.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionE = new RegionState(new PointState[]{
                new PointState(200, 250),
                new PointState(400, 250),
                new PointState(400, 500),
                new PointState(200, 500)
        });
        regionE.setLabel("E");
        regionE.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState regionF = new RegionState(new PointState[]{
                new PointState(400, 250),
                new PointState(600, 250),
                new PointState(600, 500),
                new PointState(400, 500)
        });
        regionF.setLabel("F");
        regionF.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        List<RegionState> regions = new ArrayList<RegionState>();
        regions.add(regionA);
        regions.add(regionB);
        regions.add(regionC);
        regions.add(regionD);
        regions.add(regionE);
        regions.add(regionF);

        map.setRegionsAndAddSpecials(regions);
        map.setBackgroundImageURL("file:///android_asset/map_assets/warboards_cityscape_1_and_3.jpg");
        map.setProviderURL("https://warsmithpress.wordpress.com/");
        map.setProviderName("Warsmith");
        return map;
    }


    /**
     * o---o---o---o
     * | A | B | C |
     * o---o---o---o
     * | D | E | F |
     * o---o---o---o
     * <p/>
     * Warboards cityscape 1
     *
     * @return
     */
    private static MapState createKrakenWarzoneCity() {
        MapState map = new MapState();
        map.setName("Kraken - Desert Warzone City ");
        map.setGroupName("Kraken Wargames");

        PointState pointA = new PointState(0, 0);
        PointState pointB = new PointState(270, 0);
        PointState pointC = new PointState(565, 0);
        PointState pointD = new PointState(720, 0);
        PointState pointE = new PointState(0, 156);
        PointState pointF = new PointState(200, 156);
        PointState pointG = new PointState(270, 156);
        PointState pointH = new PointState(0, 325);
        PointState pointI = new PointState(110, 325);
        PointState pointJ = new PointState(0, 480);
        PointState pointK = new PointState(150, 480);
        PointState pointL = new PointState(235, 290);
        PointState pointM = new PointState(200, 225);
        PointState pointN = new PointState(270, 225);
        PointState pointO = new PointState(565, 100);
        PointState pointP = new PointState(390, 210);
        PointState pointQ = new PointState(440, 155);
        PointState pointR = new PointState(480, 195);
        PointState pointS = new PointState(610, 160);
        PointState pointT = new PointState(720, 160);
        PointState pointU = new PointState(440, 250);
        PointState pointV = new PointState(360, 320);
        PointState pointW = new PointState(515, 320);
        PointState pointX = new PointState(440, 320);
        PointState pointY = new PointState(515, 250);
        PointState pointAA = new PointState(570, 320);
        PointState pointAC = new PointState(150, 380);
        PointState pointAB = new PointState(720, 320);
        PointState pointAD = new PointState(335, 400);
        PointState pointAE = new PointState(440, 400);
        PointState pointAF = new PointState(570, 400);
        PointState pointAG = new PointState(570, 480);
        PointState pointAH = new PointState(720, 480);


        RegionState region1 = new RegionState(new PointState[]{
                pointA, pointB, pointG, pointE
        });
        region1.setLabel("1");
        region1.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState region2 = new RegionState(new PointState[]{
                pointB, pointC, pointO, pointR, pointQ, pointP, pointU, pointX, pointV, pointN, pointG, pointB

        });
        region2.setLabel("2");
        region2.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState region3 = new RegionState(new PointState[]{
                pointC, pointD, pointT, pointS, pointO

        });
        region3.setLabel("3");
        region3.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState region4 = new RegionState(new PointState[]{
                pointE, pointF, pointM, pointI, pointH
        });
        region4.setLabel("4");
        region4.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState region5 = new RegionState(new PointState[]{
                pointF, pointG, pointN, pointV, pointX, pointAE, pointAD, pointL, pointAC, pointI, pointM
        });
        region5.setLabel("5");
        region5.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState region6 = new RegionState(new PointState[]{
                pointO, pointS, pointY, pointW, pointAA, pointAF, pointAE, pointX, pointU, pointP, pointQ, pointR
        });
        region6.setLabel("6");
        region6.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState region7 = new RegionState(new PointState[]{
                pointS, pointT, pointAB, pointAA, pointW, pointY
        });
        region7.setLabel("7");
        region7.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState region8 = new RegionState(new PointState[]{
                pointH, pointI, pointAC, pointK, pointJ
        });
        region8.setLabel("8");
        region8.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState region9 = new RegionState(new PointState[]{
                pointL, pointAD, pointAE, pointAF, pointAG, pointK, pointAC
        });
        region9.setLabel("9");
        region9.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);

        RegionState region10 = new RegionState(new PointState[]{
                pointAA, pointAB, pointAH, pointAG, pointAF
        });
        region10.setLabel("10");
        region10.setRegionType(MapState.MAP_REGION_TYPE_BUILDING_BLOCK);


        List<RegionState> regions = new ArrayList<>();
        regions.add(region1);
        regions.add(region2);
        regions.add(region3);
        regions.add(region4);
        regions.add(region5);
        regions.add(region6);
        regions.add(region7);
        regions.add(region8);
        regions.add(region9);
        regions.add(region10);


        map.setRegionsAndAddSpecials(regions);
        map.setBackgroundImageURL("http://landsofruin.com/app_assets/maps/Desert_Warzone_City_6x4.jpg");
        map.setProviderURL("https://www.fantasywelt.de/");
        map.setProviderIconURL("http://landsofruin.com/app_assets/maps/kraken_mats_logo.jpg");
        map.setProviderName("Kraken Mat");
        return map;
    }
}
