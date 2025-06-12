package fr.amu.iut.saesuper_bomberman.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import java.util.*;

/**
 * Représente l'état complet du jeu à un moment donné.
 * Cette classe centralise toutes les données du jeu :
 * - La grille de jeu avec les différents types de cases
 * - Les joueurs et leurs états
 * - Les bombes, explosions et power-ups
 * - La logique du mode Death Match
 * - La gestion des thèmes graphiques
 */
public class GameState {
    private static final int GRID_WIDTH = 17;
    private static final int GRID_HEIGHT = 13;

    private TileType[][] grid;
    private List<Player> players;
    private List<Bomb> bombs;
    private List<Explosion> explosions;
    private Set<String> playerExplosionHits;
    private List<PowerUp> powerUps;
    private long gameStartTime;
    private static final long GAME_DURATION = 180000;
    private boolean gameEnded = false;
    private boolean deathMatchActive = false;
    private int deathMatchRing = 0;
    private long lastDeathMatchUpdate = 0;
    private static final long DEATH_MATCH_INTERVAL = 4000; // 3 secondes entre chaque avancée de murs
    private static final int DEATH_MATCH_MAX_RINGS = (GRID_WIDTH < GRID_HEIGHT ? GRID_WIDTH : GRID_HEIGHT) / 2;
    private String currentTheme = "/fr/amu/iut/saesuper_bomberman/assets/bomberman/";
    public static final String DEFAULT_THEME = "/fr/amu/iut/saesuper_bomberman/assets/bomberman/";
    public static final String SPECIAL_THEME = "/fr/amu/iut/saesuper_bomberman/assets/themespecial1/";
    public static final String SPECIAL_THEME2 = "/fr/amu/iut/saesuper_bomberman/assets/themespecial2/";

    // Images pour le rendu
    private Image grassImage;
    private Image wallDestructibleImage;
    private Image wallIndestructibleImage;
    private Image bombImage;
    private Image explosionImage;
    private Image player1Image;
    private Image player2Image;
    private Image player3Image;
    private Image player4Image;
    private Image extraBombImage;
    private Image explosionExpanderImage;
    private Image maximumExplosionImage;

    /**
     * Crée un nouvel état de jeu avec tous les éléments initialisés.
     * Génère la grille de jeu, place les joueurs et initialise le chronomètre.
     */
    public GameState() {
        loadImages();
        initializeGrid();
        initializePlayers();
        bombs = new ArrayList<>();
        explosions = new ArrayList<>();
        powerUps = new ArrayList<>();
        playerExplosionHits = new HashSet<>();
        gameStartTime = System.currentTimeMillis();
    }

    /**
     * Charge les images utilisées pour le rendu graphique du jeu.
     * Ces images dépendent du thème actif.
     */
    private void loadImages() {
        try {
            String basePath = currentTheme;

            // Charger les images des tuiles
            grassImage = new Image(getClass().getResourceAsStream(basePath + "grass.png"));
            wallDestructibleImage = new Image(getClass().getResourceAsStream(basePath + "wall_destructible.png"));
            wallIndestructibleImage = new Image(getClass().getResourceAsStream(basePath + "wall_indestructible.png"));

            // Charger les images des éléments de jeu
            bombImage = new Image(getClass().getResourceAsStream(basePath + "bomb.png"));
            explosionImage = new Image(getClass().getResourceAsStream(basePath + "explosion.png"));

            // Charger les images des joueurs
            player1Image = new Image(getClass().getResourceAsStream(basePath + "player1.png"));
            player2Image = new Image(getClass().getResourceAsStream(basePath + "player2.png"));
            player3Image = new Image(getClass().getResourceAsStream(basePath + "player3.png"));
            player4Image = new Image(getClass().getResourceAsStream(basePath + "player4.png"));

            // Charger les images des power-ups
            extraBombImage = new Image(getClass().getResourceAsStream(basePath + "powerup_bomb.png"));
            explosionExpanderImage = new Image(getClass().getResourceAsStream(basePath + "powerup_flame.png"));
            maximumExplosionImage = new Image(getClass().getResourceAsStream(basePath + "powerup_maxflame.png"));

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialise la grille de jeu avec des murs et des espaces vides.
     * Génère un labyrinthe aléatoire mais équilibré pour le gameplay.
     */
    private void initializeGrid() {
        grid = new TileType[GRID_WIDTH][GRID_HEIGHT];

        // Remplir avec de l'herbe
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                grid[x][y] = TileType.GRASS;
            }
        }

        // Placer les murs indestructibles (bordures)
        for (int x = 0; x < GRID_WIDTH; x++) {
            grid[x][0] = TileType.WALL_INDESTRUCTIBLE; // Bordure haute
            grid[x][GRID_HEIGHT - 1] = TileType.WALL_INDESTRUCTIBLE; // Bordure basse
        }
        for (int y = 0; y < GRID_HEIGHT; y++) {
            grid[0][y] = TileType.WALL_INDESTRUCTIBLE; // Bordure gauche
            grid[GRID_WIDTH - 1][y] = TileType.WALL_INDESTRUCTIBLE; // Bordure droite
        }

        // Murs intérieurs indestructibles (pattern en damier)
        for (int x = 2; x < GRID_WIDTH - 1; x += 2) {
            for (int y = 2; y < GRID_HEIGHT - 1; y += 2) {
                grid[x][y] = TileType.WALL_INDESTRUCTIBLE;
            }
        }

        // Phase 1: Placer des murs destructibles avec une très haute probabilité
        Random random = new Random();
        for (int x = 1; x < GRID_WIDTH - 1; x++) {
            for (int y = 1; y < GRID_HEIGHT - 1; y++) {
                if (grid[x][y] == TileType.GRASS && random.nextDouble() < 0.70) {
                    if (!isNearStartPosition(x, y)) {
                        grid[x][y] = TileType.WALL_DESTRUCTIBLE;
                    }
                }
            }
        }

        // Phase 2: Ajouter encore plus de murs stratégiques
        addMassiveStrategicWalls(random);

        // Phase 3: Remplir les derniers espaces vides (sauf zones critiques)
        fillRemainingSpaces(random);

        // Phase 4: Vérifier qu'on a toujours des chemins viables
        ensureMinimalPaths();
    }

    /**
     * Ajoute des murs destructibles supplémentaires de manière stratégique.
     * Augmente la complexité du labyrinthe tout en maintenant un équilibre.
     *
     * @param random Le générateur de nombres aléatoires à utiliser
     */
    private void addMassiveStrategicWalls(Random random) {
        // Ajouter beaucoup plus de murs destructibles de manière stratégique
        int additionalWalls = 20; // Augmenté de 8 à 20 murs supplémentaires
        int attempts = 0;
        int placed = 0;

        while (placed < additionalWalls && attempts < 100) {
            int x = random.nextInt(GRID_WIDTH - 2) + 1;
            int y = random.nextInt(GRID_HEIGHT - 2) + 1;

            if (grid[x][y] == TileType.GRASS && !isNearStartPosition(x, y)) {
                if (!wouldCompletelyBlock(x, y)) {
                    grid[x][y] = TileType.WALL_DESTRUCTIBLE;
                    placed++;
                }
            }
            attempts++;
        }
    }

    /**
     * Change le thème graphique du jeu.
     * Recharge toutes les images avec le nouveau thème.
     *
     * @param themePath Le chemin d'accès au nouveau thème
     */
    public void changeTheme(String themePath) {
        this.currentTheme = themePath;
        loadImages(); // Recharger les images avec le nouveau thème
        System.out.println("Thème changé: " + themePath);
    }

    /**
     * Remplit les espaces restants de la grille avec des murs destructibles.
     * Assure une densité appropriée d'obstacles.
     *
     * @param random Le générateur de nombres aléatoires à utiliser
     */
    private void fillRemainingSpaces(Random random) {
        // Remplir agressivement les espaces restants
        for (int x = 1; x < GRID_WIDTH - 1; x++) {
            for (int y = 1; y < GRID_HEIGHT - 1; y++) {
                if (grid[x][y] == TileType.GRASS && !isNearStartPosition(x, y)) {
                    // 80% de chance de placer un mur même dans les espaces restants
                    if (random.nextDouble() < 0.80) {
                        if (!wouldCompletelyBlock(x, y)) {
                            grid[x][y] = TileType.WALL_DESTRUCTIBLE;
                        }
                    }
                }
            }
        }
    }

    /**
     * S'assure qu'il existe des chemins minimaux dans le labyrinthe.
     * Garantit que chaque joueur peut se déplacer dès le début.
     */
    private void ensureMinimalPaths() {
        // S'assurer qu'il y a au moins un chemin libre immédiat depuis chaque spawn
        int[][] startPositions = {
                {1, 1}, {GRID_WIDTH - 2, 1}, {1, GRID_HEIGHT - 2}, {GRID_WIDTH - 2, GRID_HEIGHT - 2}
        };

        for (int[] pos : startPositions) {
            int x = pos[0];
            int y = pos[1];

            // Garantir au moins une direction libre depuis le spawn
            int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
            boolean hasFreePath = false;

            for (int[] dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];
                if (nx > 0 && nx < GRID_WIDTH - 1 && ny > 0 && ny < GRID_HEIGHT - 1) {
                    if (grid[nx][ny] == TileType.GRASS) {
                        hasFreePath = true;
                        break;
                    }
                }
            }

            // Si aucun chemin libre, en créer un
            if (!hasFreePath) {
                for (int[] dir : directions) {
                    int nx = x + dir[0];
                    int ny = y + dir[1];
                    if (nx > 0 && nx < GRID_WIDTH - 1 && ny > 0 && ny < GRID_HEIGHT - 1) {
                        if (grid[nx][ny] == TileType.WALL_DESTRUCTIBLE) {
                            grid[nx][ny] = TileType.GRASS;
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Vérifie si placer un mur à une position donnée bloquerait complètement le passage.
     *
     * @param x La coordonnée X à vérifier
     * @param y La coordonnée Y à vérifier
     * @return true si un mur bloquerait complètement le passage, false sinon
     */
    private boolean wouldCompletelyBlock(int x, int y) {
        // Version plus permissive que wouldBlockPassage
        int adjacentSolids = 0;
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (nx >= 0 && nx < GRID_WIDTH && ny >= 0 && ny < GRID_HEIGHT) {
                if (grid[nx][ny] != TileType.GRASS) {
                    adjacentSolids++;
                }
            }
        }

        // Plus permissif: éviter seulement si TOUS les côtés sont bloqués
        return adjacentSolids >= 4;
    }

    /**
     * Vérifie si une position est proche d'un point de départ de joueur.
     * Évite de placer des obstacles près des zones de spawn.
     *
     * @param x La coordonnée X à vérifier
     * @param y La coordonnée Y à vérifier
     * @return true si la position est proche d'un point de départ, false sinon
     */
    private boolean isNearStartPosition(int x, int y) {
        // Zone de protection réduite autour des spawns (seulement la case immédiate)
        int[][] startPositions = {
                {1, 1}, {GRID_WIDTH - 2, 1}, {1, GRID_HEIGHT - 2}, {GRID_WIDTH - 2, GRID_HEIGHT - 2}
        };

        for (int[] pos : startPositions) {
            // Réduire la zone de protection à seulement 1 case de distance
            if (Math.abs(x - pos[0]) <= 0 && Math.abs(y - pos[1]) <= 0) {
                return true;
            }
            // Mais garder au moins une case libre adjacente
            if (Math.abs(x - pos[0]) <= 1 && Math.abs(y - pos[1]) <= 1 &&
                    (Math.abs(x - pos[0]) + Math.abs(y - pos[1])) == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Initialise les joueurs et leurs positions de départ.
     * Place les joueurs aux quatre coins de la carte.
     */
    private void initializePlayers() {
        players = new ArrayList<>();
        Color[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.PURPLE};

        int[][] startPositions = {
                {1, 1}, {GRID_WIDTH - 2, 1}, {1, GRID_HEIGHT - 2}, {GRID_WIDTH - 2, GRID_HEIGHT - 2}
        };

        for (int i = 0; i < 4; i++) {
            players.add(new Player(i + 1, startPositions[i][0], startPositions[i][1], colors[i]));
        }
    }

    /**
     * Met à jour l'état de toutes les bombes, gère les explosions et leurs effets.
     * Traite les explosions en chaîne et les interactions avec l'environnement.
     */
    public void updateBombs() {
        // Utiliser une liste temporaire pour éviter les modifications concurrentes
        List<Bomb> bombsToExplode = new ArrayList<>();

        Iterator<Bomb> bombIterator = bombs.iterator();
        while (bombIterator.hasNext()) {
            Bomb bomb = bombIterator.next();
            bomb.update();
            if (bomb.shouldExplode()) {
                bombsToExplode.add(bomb);
                bombIterator.remove();
            }
        }

        // Faire exploser toutes les bombes qui doivent exploser
        for (Bomb bomb : bombsToExplode) {
            explodeBomb(bomb);
            bomb.explode();
        }

        // Nettoyer les explosions finies
        Iterator<Explosion> explosionIterator = explosions.iterator();
        while (explosionIterator.hasNext()) {
            Explosion explosion = explosionIterator.next();
            explosion.update();
            if (explosion.isFinished()) {
                cleanupExplosionHits(explosion);
                explosionIterator.remove();
            }
        }
    }

    /**
     * Fait exploser une bombe et gère les effets de l'explosion.
     * Crée les explosions, détruit les murs et déclenche les explosions en chaîne.
     *
     * @param bomb La bombe qui explose
     */
    private void explodeBomb(Bomb bomb) {
        // Liste des bombes à faire exploser en chaîne
        Queue<Bomb> chainExplosions = new LinkedList<>();
        Set<String> explodedPositions = new HashSet<>();

        chainExplosions.offer(bomb);

        while (!chainExplosions.isEmpty()) {
            Bomb currentBomb = chainExplosions.poll();
            int x = currentBomb.getX();
            int y = currentBomb.getY();
            String posKey = x + "," + y;

            // Éviter d'exploser la même position plusieurs fois
            if (explodedPositions.contains(posKey)) {
                continue;
            }
            explodedPositions.add(posKey);

            int range = currentBomb.getRange();

            // Créer l'explosion centrale
            Explosion centerExplosion = new Explosion(x, y, 30);
            centerExplosion.setBombOwner(currentBomb.getOwner());
            explosions.add(centerExplosion);

            // Détruire les power-ups à la position centrale
            destroyPowerUpAt(x, y);

            // Propager l'explosion dans les 4 directions
            int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
            for (int[] dir : directions) {
                for (int i = 1; i <= range; i++) {
                    int newX = x + dir[0] * i;
                    int newY = y + dir[1] * i;

                    if (newX < 0 || newX >= GRID_WIDTH || newY < 0 || newY >= GRID_HEIGHT) {
                        break;
                    }

                    TileType tile = grid[newX][newY];

                    if (tile == TileType.WALL_INDESTRUCTIBLE) {
                        break;
                    }

                    // Créer l'explosion à cette position
                    Explosion explosion = new Explosion(newX, newY, 30);
                    explosion.setBombOwner(currentBomb.getOwner());
                    explosions.add(explosion);

                    // Chercher des bombes à faire exploser en chaîne
                    findAndQueueChainBombs(newX, newY, currentBomb.getOwner(), chainExplosions, explodedPositions);

                    // Détruire les power-ups à cette position
                    destroyPowerUpAt(newX, newY);

                    if (tile == TileType.WALL_DESTRUCTIBLE) {
                        grid[newX][newY] = TileType.GRASS;

                        // Possibilité de générer un power-up lorsqu'un mur est détruit
                        generatePowerUp(newX, newY);

                        break;
                    }
                }
            }
        }
    }

    /**
     * Détruit un power-up aux coordonnées spécifiées.
     *
     * @param x La coordonnée X du power-up
     * @param y La coordonnée Y du power-up
     */
    private void destroyPowerUpAt(int x, int y) {
        Iterator<PowerUp> iterator = powerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();
            if (powerUp.getX() == x && powerUp.getY() == y) {
                iterator.remove();
                break; // Un seul power-up par case
            }
        }
    }

    /**
     * Génère aléatoirement un power-up aux coordonnées spécifiées.
     * Les power-ups apparaissent lorsqu'un mur destructible est détruit.
     *
     * @param x La coordonnée X où générer le power-up
     * @param y La coordonnée Y où générer le power-up
     */
    private void generatePowerUp(int x, int y) {
        Random random = new Random();

        // 15% de chance de générer un power-up
        if (random.nextDouble() < 0.15) {
            double powerUpRoll = random.nextDouble();
            PowerUpType type;

            // Distribution: 45% Extra Bomb, 45% Explosion Expander, 10% Maximum Explosion
            if (powerUpRoll < 0.45) {
                type = PowerUpType.EXTRA_BOMB;
            } else if (powerUpRoll < 0.90) {
                type = PowerUpType.EXPLOSION_EXPANDER;
            } else {
                type = PowerUpType.MAXIMUM_EXPLOSION; // Rare
            }

            powerUps.add(new PowerUp(x, y, type));
        }
    }

    /**
     * Vérifie les collisions entre les joueurs et les power-ups.
     * Applique les effets des power-ups collectés.
     */
    public void checkPowerUpCollisions() {
        Iterator<PowerUp> iterator = powerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();

            for (Player player : players) {
                if (player.isAlive() && player.getX() == powerUp.getX() && player.getY() == powerUp.getY()) {
                    applyPowerUp(player, powerUp);
                    iterator.remove();
                    break;
                }
            }
        }
    }

    /**
     * Applique l'effet d'un power-up à un joueur.
     *
     * @param player Le joueur qui collecte le power-up
     * @param powerUp Le power-up à appliquer
     */
    private void applyPowerUp(Player player, PowerUp powerUp) {
        switch (powerUp.getType()) {
            case EXTRA_BOMB:
                player.increaseMaxBombs();
                System.out.println("Joueur " + player.getId() + " a obtenu un Extra Bomb! Bombes max: " + player.getMaxBombs());
                break;

            case EXPLOSION_EXPANDER:
                player.increaseBombRange();
                System.out.println("Joueur " + player.getId() + " a obtenu un Explosion Expander! Portée: " + player.getBombRange());
                break;

            case MAXIMUM_EXPLOSION:
                player.setMaximumExplosion();
                System.out.println("Joueur " + player.getId() + " a obtenu un Maximum Explosion! Portée maximale!");
                break;
        }
    }

    /**
     * Recherche et ajoute les bombes à faire exploser en chaîne.
     *
     * @param x La coordonnée X de l'explosion
     * @param y La coordonnée Y de l'explosion
     * @param originalOwner Le propriétaire de la bombe originale
     * @param chainExplosions La file d'attente des bombes à faire exploser
     * @param explodedPositions L'ensemble des positions déjà explosées
     */
    private void findAndQueueChainBombs(int x, int y, Player originalOwner,
                                        Queue<Bomb> chainExplosions, Set<String> explodedPositions) {
        String posKey = x + "," + y;

        // Si cette position a déjà explosé, ignorer
        if (explodedPositions.contains(posKey)) {
            return;
        }

        // Chercher des bombes à cette position
        Iterator<Bomb> bombIterator = bombs.iterator();
        while (bombIterator.hasNext()) {
            Bomb bomb = bombIterator.next();
            if (bomb.getX() == x && bomb.getY() == y) {
                // Créer une nouvelle bombe pour la chaîne avec le propriétaire original
                Bomb chainBomb = new Bomb(bomb.getX(), bomb.getY(), bomb.getRange(), originalOwner);
                chainExplosions.offer(chainBomb);

                // Retirer la bombe originale de la liste
                bomb.explode();
                bombIterator.remove();
                break;
            }
        }
    }

    /**
     * Nettoie les collisions d'explosion enregistrées pour une explosion terminée.
     *
     * @param explosion L'explosion terminée
     */
    private void cleanupExplosionHits(Explosion explosion) {
        String explosionKey = explosion.getX() + "," + explosion.getY();
        playerExplosionHits.removeIf(hit -> hit.startsWith(explosionKey));
    }

    /**
     * Vérifie les collisions entre les joueurs et les explosions.
     * Gère les dégâts et les éliminations.
     */
    public void checkExplosionCollisions() {
        for (Explosion explosion : explosions) {
            for (Player player : players) {
                if (player.isAlive() && player.getX() == explosion.getX() && player.getY() == explosion.getY()) {
                    String hitKey = explosion.getX() + "," + explosion.getY() + "-" + player.getId();

                    if (!playerExplosionHits.contains(hitKey)) {
                        if (player.takeDamage()) {
                            playerExplosionHits.add(hitKey);

                            Player bombOwner = explosion.getBombOwner();
                            if (bombOwner != null && bombOwner != player && bombOwner.isAlive()) {
                                bombOwner.addKill();
                                System.out.println("Joueur " + bombOwner.getId() + " a éliminé le joueur " + player.getId() +
                                        " (Total kills: " + bombOwner.getKillCount() + ")");
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Renvoie le temps restant avant la fin de la partie normale.
     *
     * @return Le temps restant en millisecondes
     */
    public long getTimeRemaining() {
        if (gameEnded) return 0;
        long elapsed = System.currentTimeMillis() - gameStartTime;
        return Math.max(0, GAME_DURATION - elapsed);
    }

    /**
     * Vérifie si le temps réglementaire est écoulé.
     *
     * @return true si le temps est écoulé, false sinon
     */
    public boolean isTimeUp() {
        return !gameEnded && getTimeRemaining() <= 0;
    }

    /**
     * Active le mode Death Match qui réduit progressivement la taille de l'arène.
     * Ce mode s'active automatiquement à la fin du temps réglementaire.
     */
    public void startDeathMatch() {
        if (!deathMatchActive) {
            deathMatchActive = true;
            deathMatchRing = 0;
            lastDeathMatchUpdate = System.currentTimeMillis();
            System.out.println("DEATH MATCH ACTIVÉ!");
        }
    }

    /**
     * Vérifie si le mode Death Match est actif.
     *
     * @return true si le Death Match est actif, false sinon
     */
    public boolean isDeathMatchActive() {
        return deathMatchActive;
    }

    /**
     * Met à jour l'état du Death Match, réduisant l'arène à intervalles réguliers.
     * Transforme progressivement les bordures en murs indestructibles.
     */
    public void updateDeathMatch() {
        if (!deathMatchActive) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDeathMatchUpdate >= DEATH_MATCH_INTERVAL) {
            advanceDeathMatchRing();
            lastDeathMatchUpdate = currentTime;
        }
    }

    /**
     * Fait avancer le niveau de contraction de l'arène en mode Death Match.
     * Ajoute une nouvelle couche de murs indestructibles vers le centre.
     */
    private void advanceDeathMatchRing() {
        if (deathMatchRing >= DEATH_MATCH_MAX_RINGS) {
            return; // Arrêter quand on atteint le centre
        }

        // Avancer d'un niveau de contraction
        deathMatchRing++;

        // Transformer les bordures en murs indestructibles
        int ringX = deathMatchRing;
        int ringY = deathMatchRing;
        int maxX = GRID_WIDTH - 1 - ringX;
        int maxY = GRID_HEIGHT - 1 - ringY;

        // Transformer le périmètre actuel en murs indestructibles
        for (int x = ringX; x <= maxX; x++) {
            // Murs horizontaux (haut et bas)
            checkAndTransformToWall(x, ringY);
            checkAndTransformToWall(x, maxY);
        }

        for (int y = ringY + 1; y < maxY; y++) {
            // Murs verticaux (gauche et droite)
            checkAndTransformToWall(ringX, y);
            checkAndTransformToWall(maxX, y);
        }

        System.out.println("DEATH MATCH: L'arène se réduit! Niveau " + deathMatchRing);
    }

    /**
     * Vérifie et transforme une case en mur indestructible pour le Death Match.
     * Élimine les joueurs qui se trouvent sur cette case.
     *
     * @param x La coordonnée X de la case
     * @param y La coordonnée Y de la case
     */
    private void checkAndTransformToWall(int x, int y) {
        // Vérifier si un joueur est sur cette case
        for (Player player : players) {
            if (player.isAlive() && player.getX() == x && player.getY() == y) {
                player.killByDeathMatch();
                System.out.println("Joueur " + player.getId() + " a été écrasé par les murs!");
            }
        }

        // Détruire tout power-up à cette position
        destroyPowerUpAt(x, y);

        // Transformer en mur indestructible
        grid[x][y] = TileType.WALL_INDESTRUCTIBLE;
    }

    /**
     * Renvoie le temps restant au format MM:SS.
     *
     * @return Le temps formaté sous forme de chaîne
     */
    public String getFormattedTimeRemaining() {
        long timeLeft = getTimeRemaining();
        long minutes = timeLeft / 60000;
        long seconds = (timeLeft % 60000) / 1000;
        return String.format("%d:%02d", minutes, seconds);
    }

    /**
     * Dessine l'état actuel du jeu sur le contexte graphique spécifié.
     * Affiche la grille, les joueurs, les bombes, les explosions et les power-ups.
     *
     * @param gc Le contexte graphique sur lequel dessiner
     * @param tileSize La taille d'une case en pixels
     */
    public void render(GraphicsContext gc, int tileSize) {
        // Effacer l'écran avec une couleur de fond
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.setFill(Color.DARKGREEN);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // Dessiner la grille avec des images
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                Image tileImage = null;
                Color fallbackColor = null;

                switch (grid[x][y]) {
                    case GRASS:
                        tileImage = grassImage;
                        fallbackColor = Color.LIGHTGREEN;
                        break;
                    case WALL_DESTRUCTIBLE:
                        tileImage = wallDestructibleImage;
                        fallbackColor = Color.BROWN;
                        break;
                    case WALL_INDESTRUCTIBLE:
                        tileImage = wallIndestructibleImage;
                        fallbackColor = Color.GRAY;
                        break;
                }

                // Utiliser l'image si disponible, sinon utiliser la couleur de fallback
                if (tileImage != null && !tileImage.isError()) {
                    gc.drawImage(tileImage, x * tileSize, y * tileSize, tileSize, tileSize);
                } else {
                    gc.setFill(fallbackColor);
                    gc.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
                    gc.setStroke(Color.BLACK);
                    gc.strokeRect(x * tileSize, y * tileSize, tileSize, tileSize);
                }
            }
        }

        // Dessiner les power-ups
        for (PowerUp powerUp : powerUps) {
            Image powerUpImage = null;
            Color fallbackColor = null;

            switch (powerUp.getType()) {
                case EXTRA_BOMB:
                    powerUpImage = extraBombImage;
                    fallbackColor = Color.BLUE;
                    break;

                case EXPLOSION_EXPANDER:
                    powerUpImage = explosionExpanderImage;
                    fallbackColor = Color.RED;
                    break;

                case MAXIMUM_EXPLOSION:
                    powerUpImage = maximumExplosionImage;
                    fallbackColor = Color.YELLOW;
                    break;
            }

            if (powerUpImage != null && !powerUpImage.isError()) {
                gc.drawImage(powerUpImage, powerUp.getX() * tileSize, powerUp.getY() * tileSize, tileSize, tileSize);
            } else {
                // Fallback si l'image n'est pas disponible
                gc.setFill(fallbackColor);
                gc.fillOval(powerUp.getX() * tileSize + 5, powerUp.getY() * tileSize + 5,
                        tileSize - 10, tileSize - 10);
                gc.setStroke(Color.WHITE);
                gc.strokeOval(powerUp.getX() * tileSize + 5, powerUp.getY() * tileSize + 5,
                        tileSize - 10, tileSize - 10);
            }
        }

        // Dessiner les explosions
        for (Explosion explosion : explosions) {
            double intensity = (double) explosion.getTimeLeft() / explosion.getDuration();

            if (explosionImage != null && !explosionImage.isError()) {
                // Utiliser l'image d'explosion avec transparence
                gc.save();
                gc.setGlobalAlpha(intensity);
                gc.drawImage(explosionImage, explosion.getX() * tileSize, explosion.getY() * tileSize, tileSize, tileSize);
                gc.restore();
            } else {
                // Fallback: utiliser les rectangles colorés
                Color explosionColor = Color.color(1.0, 0.5, 0.0, intensity);
                gc.setFill(explosionColor);
                gc.fillRect(explosion.getX() * tileSize + 2, explosion.getY() * tileSize + 2,
                        tileSize - 4, tileSize - 4);

                gc.setStroke(Color.color(1.0, 0.0, 0.0, intensity));
                gc.setLineWidth(2);
                gc.strokeRect(explosion.getX() * tileSize + 2, explosion.getY() * tileSize + 2,
                        tileSize - 4, tileSize - 4);
            }
        }

        // Dessiner les bombes
        for (Bomb bomb : bombs) {
            if (bombImage != null && !bombImage.isError()) {
                // Utiliser l'image de bombe
                gc.drawImage(bombImage, bomb.getX() * tileSize, bomb.getY() * tileSize, tileSize, tileSize);

                // Ajouter un indicateur de temps (petit cercle qui rétrécit)
                long timeLeft = bomb.getTimeLeft();
                double progress = timeLeft / 2000.0; // Adapté pour 2 secondes
                if (progress > 0) {
                    gc.setFill(Color.RED);
                    double indicatorSize = 8 * progress;
                    // Position en haut à droite, presque au bord
                    gc.fillOval(bomb.getX() * tileSize + tileSize - indicatorSize - 2,
                            bomb.getY() * tileSize + 2,
                            indicatorSize, indicatorSize);
                }
            } else {
                // Fallback: utiliser les formes géométriques
                gc.setFill(Color.BLACK);
                gc.fillOval(bomb.getX() * tileSize + 5, bomb.getY() * tileSize + 5,
                        tileSize - 10, tileSize - 10);

                long timeLeft = bomb.getTimeLeft();
                double progress = timeLeft / 2000.0; // Adapté pour 2 secondes
                gc.setFill(Color.RED);
                double wickSize = (tileSize - 16) * progress;
                gc.fillOval(bomb.getX() * tileSize + tileSize - wickSize - 2,
                        bomb.getY() * tileSize + 2,
                        wickSize, wickSize);
            }
        }

        // Dessiner les joueurs
        for (Player player : players) {
            if (player.isAlive()) {
                Image playerImage = getPlayerImage(player.getId());

                if (playerImage != null && !playerImage.isError()) {
                    // Utiliser l'image du joueur
                    if (player.isImmune()) {
                        // Effet de clignotement pour l'immunité
                        long time = System.currentTimeMillis();
                        if ((time / 100) % 2 == 0) {
                            gc.save();
                            gc.setGlobalAlpha(0.5);
                            gc.drawImage(playerImage, player.getX() * tileSize, player.getY() * tileSize, tileSize, tileSize);
                            gc.restore();
                        } else {
                            gc.drawImage(playerImage, player.getX() * tileSize, player.getY() * tileSize, tileSize, tileSize);
                        }
                    } else {
                        gc.drawImage(playerImage, player.getX() * tileSize, player.getY() * tileSize, tileSize, tileSize);
                    }
                } else {
                    // Fallback: utiliser les cercles colorés
                    if (player.isImmune()) {
                        long time = System.currentTimeMillis();
                        if ((time / 100) % 2 == 0) {
                            gc.setFill(player.getColor().deriveColor(0, 1, 1, 0.5));
                        } else {
                            gc.setFill(player.getColor());
                        }
                    } else {
                        gc.setFill(player.getColor());
                    }

                    gc.fillOval(player.getX() * tileSize + 3, player.getY() * tileSize + 3,
                            tileSize - 6, tileSize - 6);

                    gc.setStroke(Color.WHITE);
                    gc.setLineWidth(1);
                    gc.strokeOval(player.getX() * tileSize + 3, player.getY() * tileSize + 3,
                            tileSize - 6, tileSize - 6);
                }
            }
        }
    }

    /**
     * Récupère l'image correspondant à un joueur donné.
     *
     * @param playerId L'identifiant du joueur (1-4)
     * @return L'image du joueur ou null si l'identifiant est invalide
     */
    private Image getPlayerImage(int playerId) {
        switch (playerId) {
            case 1: return player1Image;
            case 2: return player2Image;
            case 3: return player3Image;
            case 4: return player4Image;
            default: return null;
        }
    }

    /**
     * Vérifie s'il y a une bombe aux coordonnées spécifiées.
     *
     * @param x La coordonnée X à vérifier
     * @param y La coordonnée Y à vérifier
     * @return true s'il y a une bombe à cette position, false sinon
     */
    public boolean hasBombAt(int x, int y) {
        for (Bomb bomb : bombs) {
            if (bomb.getX() == x && bomb.getY() == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Renvoie le type de tuile aux coordonnées spécifiées.
     *
     * @param x La coordonnée X à vérifier
     * @param y La coordonnée Y à vérifier
     * @return Le type de tuile à cette position
     */
    public TileType getTile(int x, int y) {
        if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) {
            return TileType.WALL_INDESTRUCTIBLE;
        }
        return grid[x][y];
    }

    /**
     * Renvoie la liste des joueurs.
     *
     * @return La liste des joueurs
     */
    public List<Player> getPlayers() { return players; }

    /**
     * Renvoie la liste des bombes actives.
     *
     * @return La liste des bombes
     */
    public List<Bomb> getBombs() { return bombs; }

    /**
     * Ajoute une bombe à la liste des bombes actives.
     *
     * @param bomb La bombe à ajouter
     */
    public void addBomb(Bomb bomb) { bombs.add(bomb); }

    /**
     * Vérifie si un joueur peut se déplacer aux coordonnées spécifiées.
     *
     * @param x La coordonnée X à vérifier
     * @param y La coordonnée Y à vérifier
     * @return true si le déplacement est possible, false sinon
     */
    public boolean canMoveTo(int x, int y) {
        if (getTile(x, y) != TileType.GRASS) {
            return false;
        }

        if (hasBombAt(x, y)) {
            return false;
        }

        for (Player player : players) {
            if (player.isAlive() && player.getX() == x && player.getY() == y) {
                return false;
            }
        }

        return true;
    }
}