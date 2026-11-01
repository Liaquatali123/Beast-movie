package com.example.data.repository

import com.example.data.local.BeastMoviesDatabase
import com.example.data.local.ContinueWatchingEntity
import com.example.data.local.RecentSearchEntity
import com.example.data.local.WatchHistoryEntity
import com.example.data.local.WatchlistEntity
import com.example.data.model.CastMember
import com.example.data.model.Episode
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.model.Review
import com.example.data.model.Season
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MovieRepository(private val database: BeastMoviesDatabase) {

    private val watchlistDao = database.watchlistDao()
    private val watchHistoryDao = database.watchHistoryDao()
    private val continueWatchingDao = database.continueWatchingDao()
    private val recentSearchDao = database.recentSearchDao()

    // Working high-reliability video streams
    private val streamTearsOfSteel = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
    private val streamBigBuckBunny = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    private val streamElephantsDream = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
    private val streamSintel = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
    private val streamForBiggerBlazes = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
    private val streamForBiggerEscapes = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"

    private val allMediaItems = listOf(
        // ==================== MOVIES ====================
        MediaItem(
            id = "dune-2",
            title = "Dune: Part Two",
            type = MediaType.MOVIE,
            synopsis = "Paul Atreides unites with Chani and the Fremen while facing a new threat on Arrakis. As he seeks revenge against the conspirators who destroyed his family, he faces a choice between the love of his life and the fate of the universe.",
            posterUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1200&auto=format&fit=crop&q=80",
            releaseYear = "2024",
            rating = 8.8,
            genres = listOf("Action", "Adventure", "Drama", "Sci-Fi"),
            runtime = "2h 46m",
            ageRating = "PG-13",
            isFeatured = true,
            isTrending = true,
            isPopular = true,
            streamUrl = streamTearsOfSteel,
            cast = listOf(
                CastMember("1", "Timothée Chalamet", "Paul Atreides", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&auto=format&fit=crop&q=80"),
                CastMember("2", "Zendaya", "Chani", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&auto=format&fit=crop&q=80"),
                CastMember("3", "Rebecca Ferguson", "Lady Jessica", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80"),
                CastMember("4", "Javier Bardem", "Stilgar", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&auto=format&fit=crop&q=80")
            ),
            reviews = listOf(
                Review("r1", "Alex Vance", 9.5, "March 2024", "A cinematic masterpiece. Denis Villeneuve has created an unforgettable sci-fi epic with unmatched visual and sound design."),
                Review("r2", "Sarah Connor", 9.0, "April 2024", "The scale, intensity, and performances are breathtaking. A true modern classic.")
            )
        ),
        MediaItem(
            id = "john-wick-4",
            title = "John Wick: Chapter 4",
            type = MediaType.MOVIE,
            synopsis = "John Wick uncovers a path to defeating The High Table. But before he can earn his freedom, Wick must face off against a new enemy with powerful alliances across the globe and forces that turn old friends into foes.",
            posterUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1200&auto=format&fit=crop&q=80",
            releaseYear = "2023",
            rating = 8.7,
            genres = listOf("Action", "Crime", "Thriller"),
            runtime = "2h 49m",
            ageRating = "R",
            isFeatured = true,
            isTrending = true,
            isPopular = true,
            streamUrl = streamBigBuckBunny,
            cast = listOf(
                CastMember("jw1", "Keanu Reeves", "John Wick", "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=300&auto=format&fit=crop&q=80"),
                CastMember("jw2", "Donnie Yen", "Caine", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=300&auto=format&fit=crop&q=80"),
                CastMember("jw3", "Bill Skarsgård", "Marquis", "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=300&auto=format&fit=crop&q=80")
            ),
            reviews = listOf(
                Review("jw-r1", "Marcus Brody", 9.0, "May 2023", "Non-stop heart-pounding action choreography. Keanu Reeves and Donnie Yen are magnificent together.")
            )
        ),
        MediaItem(
            id = "joker",
            title = "Joker",
            type = MediaType.MOVIE,
            synopsis = "During the 1980s, a failed stand-up comedian is driven to insanity and turns to a life of crime and chaos in Gotham City while becoming an infamous psychopathic crime figure.",
            posterUrl = "https://images.unsplash.com/photo-1563089145-599997674d42?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1514565131-fce0801e5785?w=1200&auto=format&fit=crop&q=80",
            releaseYear = "2019",
            rating = 8.4,
            genres = listOf("Crime", "Drama", "Thriller"),
            runtime = "2h 02m",
            ageRating = "R",
            isFeatured = false,
            isTrending = true,
            isPopular = true,
            streamUrl = streamElephantsDream,
            cast = listOf(
                CastMember("j1", "Joaquin Phoenix", "Arthur Fleck", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&auto=format&fit=crop&q=80"),
                CastMember("j2", "Robert De Niro", "Murray Franklin", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=300&auto=format&fit=crop&q=80")
            )
        ),
        MediaItem(
            id = "avatar-2",
            title = "Avatar: The Way of Water",
            type = MediaType.MOVIE,
            synopsis = "Jake Sully lives with his newfound family formed on the extrasolar moon Pandora. Once a familiar threat returns to finish what was previously started, Jake must work with Neytiri and the army of the Na'vi race to protect their home.",
            posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1200&auto=format&fit=crop&q=80",
            releaseYear = "2022",
            rating = 7.6,
            genres = listOf("Action", "Adventure", "Fantasy", "Sci-Fi"),
            runtime = "3h 12m",
            ageRating = "PG-13",
            isFeatured = false,
            isTrending = true,
            isPopular = true,
            streamUrl = streamSintel
        ),
        MediaItem(
            id = "the-batman",
            title = "The Batman",
            type = MediaType.MOVIE,
            synopsis = "When a sadistic serial killer begins murdering key political figures in Gotham, the Batman is forced to investigate the city's hidden corruption and question his family's involvement.",
            posterUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1200&auto=format&fit=crop&q=80",
            releaseYear = "2022",
            rating = 7.8,
            genres = listOf("Action", "Crime", "Drama"),
            runtime = "2h 56m",
            ageRating = "PG-13",
            isFeatured = false,
            isTrending = true,
            isPopular = true,
            streamUrl = streamForBiggerBlazes
        ),
        MediaItem(
            id = "top-gun-maverick",
            title = "Top Gun: Maverick",
            type = MediaType.MOVIE,
            synopsis = "After thirty years, Maverick is still pushing the envelope as a top naval aviator, but must confront ghosts of his past when he leads TOP GUN's elite graduates on an impossible mission.",
            posterUrl = "https://images.unsplash.com/photo-1519074069444-1ba4ea16e877?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1200&auto=format&fit=crop&q=80",
            releaseYear = "2022",
            rating = 8.3,
            genres = listOf("Action", "Drama"),
            runtime = "2h 10m",
            ageRating = "PG-13",
            isFeatured = false,
            isTrending = false,
            isPopular = true,
            streamUrl = streamForBiggerEscapes
        ),
        MediaItem(
            id = "spiderman-across",
            title = "Spider-Man: Across the Spider-Verse",
            type = MediaType.MOVIE,
            synopsis = "Miles Morales catapults across the Multiverse, where he encounters a team of Spider-People charged with protecting its very existence.",
            posterUrl = "https://images.unsplash.com/photo-1635805737707-575885ab0820?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=1200&auto=format&fit=crop&q=80",
            releaseYear = "2023",
            rating = 8.7,
            genres = listOf("Animation", "Action", "Adventure"),
            runtime = "2h 20m",
            ageRating = "PG",
            isFeatured = false,
            isTrending = true,
            isPopular = true,
            streamUrl = streamTearsOfSteel
        ),
        MediaItem(
            id = "oppenheimer",
            title = "Oppenheimer",
            type = MediaType.MOVIE,
            synopsis = "The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb during World War II.",
            posterUrl = "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1200&auto=format&fit=crop&q=80",
            releaseYear = "2023",
            rating = 8.9,
            genres = listOf("Biography", "Drama", "History"),
            runtime = "3h 00m",
            ageRating = "R",
            isFeatured = false,
            isTrending = true,
            isPopular = true,
            streamUrl = streamBigBuckBunny
        ),
        MediaItem(
            id = "mad-max-fury",
            title = "Mad Max: Fury Road",
            type = MediaType.MOVIE,
            synopsis = "In a post-apocalyptic wasteland, a woman rebels against a tyrannical ruler in search for her homeland with the aid of a group of female prisoners and a drifter named Max.",
            posterUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1200&auto=format&fit=crop&q=80",
            releaseYear = "2015",
            rating = 8.1,
            genres = listOf("Action", "Adventure", "Sci-Fi"),
            runtime = "2h 00m",
            ageRating = "R",
            streamUrl = streamSintel
        ),
        MediaItem(
            id = "star-wars-hope",
            title = "Star Wars: A New Hope",
            type = MediaType.MOVIE,
            synopsis = "Luke Skywalker joins forces with a Jedi Knight, a cocky pilot, a Wookiee and two droids to save the galaxy from the Empire's world-destroying battle station.",
            posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1200&auto=format&fit=crop&q=80",
            releaseYear = "1977",
            rating = 8.6,
            genres = listOf("Action", "Adventure", "Fantasy"),
            runtime = "2h 01m",
            ageRating = "PG",
            streamUrl = streamElephantsDream
        ),

        // ==================== TV SERIES ====================
        MediaItem(
            id = "breaking-bad",
            title = "Breaking Bad",
            type = MediaType.SERIES,
            synopsis = "A high school chemistry teacher turned methamphetamine manufacturer. Walter White, diagnosed with stage-three lung cancer, turns to crime to secure his family's financial future.",
            posterUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1200&auto=format&fit=crop&q=80",
            releaseYear = "2008–2013",
            rating = 9.5,
            genres = listOf("Crime", "Drama", "Thriller"),
            runtime = "5 Seasons",
            ageRating = "TV-MA",
            isFeatured = false,
            isTrending = true,
            isPopular = true,
            streamUrl = streamBigBuckBunny,
            seasons = listOf(
                Season(
                    id = "bb-s1",
                    seriesId = "breaking-bad",
                    seasonNumber = 1,
                    title = "Season 1",
                    episodeCount = 7,
                    thumbnailUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
                    episodes = listOf(
                        Episode("bb-s1-e1", "bb-s1", 1, "Pilot", "Diagnosed with terminal lung cancer, chemistry teacher Walter White teams with former student Jesse Pinkman to cook meth.", "58m", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80", streamBigBuckBunny),
                        Episode("bb-s1-e2", "bb-s1", 2, "Cat's in the Bag...", "Walt and Jesse attempt to dispose of two bodies in the RV, but things quickly spiral out of control.", "48m", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80", streamTearsOfSteel),
                        Episode("bb-s1-e3", "bb-s1", 3, "...And the Bag's in the River", "Walt wrestles with a fateful moral choice regarding the captive Krazy-8 in Jesse's basement.", "48m", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400&auto=format&fit=crop&q=80", streamElephantsDream),
                        Episode("bb-s1-e4", "bb-s1", 4, "Cancer Man", "Walt reveals his cancer diagnosis to his family, while the DEA grows suspicious of a new player.", "48m", "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=400&auto=format&fit=crop&q=80", streamSintel),
                        Episode("bb-s1-e5", "bb-s1", 5, "Gray Matter", "Walt's old business partners offer financial assistance, but his pride threatens his survival.", "48m", "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=400&auto=format&fit=crop&q=80", streamForBiggerBlazes),
                        Episode("bb-s1-e6", "bb-s1", 6, "Crazy Handful of Nothin'", "Adopting the moniker 'Heisenberg', Walter makes a dangerous deal with violent kingpin Tuco Salamanca.", "48m", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80", streamForBiggerEscapes),
                        Episode("bb-s1-e7", "bb-s1", 7, "A No-Rough-Stuff-Type Deal", "Walt and Jesse expand their operation by acquiring chemical precursors through bold theft.", "48m", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80", streamBigBuckBunny)
                    )
                ),
                Season(
                    id = "bb-s2",
                    seriesId = "breaking-bad",
                    seasonNumber = 2,
                    title = "Season 2",
                    episodeCount = 13,
                    thumbnailUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80",
                    episodes = listOf(
                        Episode("bb-s2-e1", "bb-s2", 1, "Seven Thirty-Seven", "Walt and Jesse realize how terrifying Tuco can be and look for ways to protect themselves.", "47m", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80", streamTearsOfSteel),
                        Episode("bb-s2-e2", "bb-s2", 2, "Grilled", "Kidnapped by Tuco and taken to a desert hideout, Walt and Jesse face mortal peril.", "48m", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400&auto=format&fit=crop&q=80", streamBigBuckBunny)
                    )
                ),
                Season(
                    id = "bb-s3",
                    seriesId = "breaking-bad",
                    seasonNumber = 3,
                    title = "Season 3",
                    episodeCount = 13,
                    thumbnailUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400&auto=format&fit=crop&q=80",
                    episodes = listOf(
                        Episode("bb-s3-e1", "bb-s3", 1, "No Más", "Skyler contemplates divorce as Walt struggles with the consequences of his double life.", "47m", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400&auto=format&fit=crop&q=80", streamSintel)
                    )
                ),
                Season(
                    id = "bb-s4",
                    seriesId = "breaking-bad",
                    seasonNumber = 4,
                    title = "Season 4",
                    episodeCount = 13,
                    thumbnailUrl = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=400&auto=format&fit=crop&q=80"
                ),
                Season(
                    id = "bb-s5",
                    seriesId = "breaking-bad",
                    seasonNumber = 5,
                    title = "Season 5",
                    episodeCount = 16,
                    thumbnailUrl = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=400&auto=format&fit=crop&q=80"
                )
            ),
            cast = listOf(
                CastMember("c1", "Bryan Cranston", "Walter White", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&auto=format&fit=crop&q=80"),
                CastMember("c2", "Aaron Paul", "Jesse Pinkman", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&auto=format&fit=crop&q=80"),
                CastMember("c3", "Anna Gunn", "Skyler White", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80")
            )
        ),
        MediaItem(
            id = "the-last-of-us",
            title = "The Last of Us",
            type = MediaType.SERIES,
            synopsis = "After a global pandemic destroys civilization, a hardened survivor takes charge of a 14-year-old girl who may be humanity's last hope.",
            posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1200&auto=format&fit=crop&q=80",
            releaseYear = "2023",
            rating = 8.8,
            genres = listOf("Action", "Adventure", "Drama"),
            runtime = "1 Season",
            ageRating = "TV-MA",
            isFeatured = false,
            isTrending = true,
            isPopular = true,
            streamUrl = streamTearsOfSteel,
            seasons = listOf(
                Season(
                    id = "tlou-s1",
                    seriesId = "the-last-of-us",
                    seasonNumber = 1,
                    title = "Season 1",
                    episodeCount = 9,
                    thumbnailUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&auto=format&fit=crop&q=80",
                    episodes = listOf(
                        Episode("tlou-s1-e1", "tlou-s1", 1, "When You're Lost in the Darkness", "Twenty years after a fungal outbreak ravages the planet, Joel and Tess are tasked with a fateful mission.", "81m", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&auto=format&fit=crop&q=80", streamTearsOfSteel),
                        Episode("tlou-s1-e2", "tlou-s1", 2, "Infected", "Leaving the quarantine zone behind, Joel, Tess and Ellie navigate a deserted, overgrown Boston.", "53m", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&auto=format&fit=crop&q=80", streamBigBuckBunny),
                        Episode("tlou-s1-e3", "tlou-s1", 3, "Long, Long Time", "When a stranger approaches his survival compound, doomsday prepper Bill forms an unlikely bond.", "75m", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80", streamElephantsDream)
                    )
                )
            )
        ),
        MediaItem(
            id = "wednesday",
            title = "Wednesday",
            type = MediaType.SERIES,
            synopsis = "Follows Wednesday Addams' years as a student, when she attempts to master her emerging psychic ability, thwart a monstrous killing spree, and solve a mystery that embroiled her parents 25 years ago.",
            posterUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1200&auto=format&fit=crop&q=80",
            releaseYear = "2022",
            rating = 8.1,
            genres = listOf("Comedy", "Crime", "Fantasy"),
            runtime = "1 Season",
            ageRating = "TV-14",
            isFeatured = false,
            isTrending = true,
            isPopular = true,
            streamUrl = streamSintel
        ),
        MediaItem(
            id = "stranger-things",
            title = "Stranger Things",
            type = MediaType.SERIES,
            synopsis = "When a young boy vanishes, a small town uncovers a mystery involving secret experiments, terrifying supernatural forces and one strange little girl.",
            posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1200&auto=format&fit=crop&q=80",
            releaseYear = "2016–Present",
            rating = 8.7,
            genres = listOf("Drama", "Fantasy", "Horror"),
            runtime = "4 Seasons",
            ageRating = "TV-14",
            isFeatured = false,
            isTrending = true,
            isPopular = true,
            streamUrl = streamForBiggerBlazes
        ),

        // ==================== ANIME ====================
        MediaItem(
            id = "attack-on-titan",
            title = "Attack on Titan",
            type = MediaType.ANIME,
            synopsis = "After his hometown is destroyed and his mother is killed, young Eren Jaeger vows to cleanse the earth of the giant humanoid Titans that have brought humanity to the brink of extinction.",
            posterUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=1200&auto=format&fit=crop&q=80",
            releaseYear = "2013–2023",
            rating = 9.1,
            genres = listOf("Animation", "Action", "Adventure"),
            runtime = "4 Seasons",
            ageRating = "TV-MA",
            isFeatured = false,
            isTrending = true,
            isPopular = true,
            streamUrl = streamBigBuckBunny,
            seasons = listOf(
                Season(
                    id = "aot-s1",
                    seriesId = "attack-on-titan",
                    seasonNumber = 1,
                    title = "Season 1",
                    episodeCount = 25,
                    thumbnailUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=400&auto=format&fit=crop&q=80",
                    episodes = listOf(
                        Episode("aot-s1-e1", "aot-s1", 1, "To You, in 2000 Years: The Fall of Shiganshina, Part 1", "After 100 years of peace, humanity is suddenly reminded of the terror of being at the mercy of Titans.", "24m", "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=400&auto=format&fit=crop&q=80", streamBigBuckBunny),
                        Episode("aot-s1-e2", "aot-s1", 2, "That Day: The Fall of Shiganshina, Part 2", "The Shiganshina district is overwhelmed by Titans, forcing refugees to flee behind Wall Rose.", "24m", "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=400&auto=format&fit=crop&q=80", streamTearsOfSteel)
                    )
                )
            )
        ),
        MediaItem(
            id = "demon-slayer",
            title = "Demon Slayer: Kimetsu no Yaiba",
            type = MediaType.ANIME,
            synopsis = "A family is attacked by demons and only two members survive - Tanjiro and his sister Nezuko, who is turning into a demon herself. Tanjiro sets out to become a demon slayer to avenge his family and cure his sister.",
            posterUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1200&auto=format&fit=crop&q=80",
            releaseYear = "2019–Present",
            rating = 8.6,
            genres = listOf("Animation", "Action", "Fantasy"),
            runtime = "4 Seasons",
            ageRating = "TV-14",
            isFeatured = false,
            isTrending = true,
            isPopular = true,
            streamUrl = streamSintel
        ),
        MediaItem(
            id = "jujutsu-kaisen",
            title = "Jujutsu Kaisen",
            type = MediaType.ANIME,
            synopsis = "A boy swallows a cursed talisman - the finger of a demon - and becomes cursed himself. He enters a shaman's school to be able to locate the demon's other body parts and thus exorcise himself.",
            posterUrl = "https://images.unsplash.com/photo-1563089145-599997674d42?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1200&auto=format&fit=crop&q=80",
            releaseYear = "2020–Present",
            rating = 8.5,
            genres = listOf("Animation", "Action", "Supernatural"),
            runtime = "2 Seasons",
            ageRating = "TV-14",
            isFeatured = false,
            isTrending = true,
            isPopular = true,
            streamUrl = streamForBiggerBlazes
        )
    )

    fun getFeatured(): MediaItem {
        return allMediaItems.firstOrNull { it.isFeatured } ?: allMediaItems.first()
    }

    fun getTrendingMovies(): List<MediaItem> {
        return allMediaItems.filter { it.type == MediaType.MOVIE && it.isTrending }
    }

    fun getPopularSeries(): List<MediaItem> {
        return allMediaItems.filter { it.type == MediaType.SERIES }
    }

    fun getTrendingAnime(): List<MediaItem> {
        return allMediaItems.filter { it.type == MediaType.ANIME }
    }

    fun getAllMovies(): List<MediaItem> {
        return allMediaItems.filter { it.type == MediaType.MOVIE }
    }

    fun getAllSeries(): List<MediaItem> {
        return allMediaItems.filter { it.type == MediaType.SERIES }
    }

    fun getAllAnime(): List<MediaItem> {
        return allMediaItems.filter { it.type == MediaType.ANIME }
    }

    fun getById(id: String): MediaItem? {
        return allMediaItems.firstOrNull { it.id == id }
    }

    fun getSimilar(id: String): List<MediaItem> {
        val current = getById(id) ?: return allMediaItems.take(5)
        return allMediaItems.filter { it.id != id && (it.type == current.type || it.genres.any { g -> current.genres.contains(g) }) }
    }

    fun search(query: String, type: MediaType? = null, genre: String? = null): List<MediaItem> {
        val q = query.trim().lowercase()
        return allMediaItems.filter { item ->
            val matchesQuery = q.isEmpty() ||
                item.title.lowercase().contains(q) ||
                item.genres.any { it.lowercase().contains(q) } ||
                item.synopsis.lowercase().contains(q)

            val matchesType = type == null || item.type == type
            val matchesGenre = genre == null || genre.equals("All", ignoreCase = true) || item.genres.any { it.equals(genre, ignoreCase = true) }

            matchesQuery && matchesType && matchesGenre
        }
    }

    // Watchlist methods
    fun getWatchlist(): Flow<List<WatchlistEntity>> = watchlistDao.getAll()

    fun isInWatchlist(id: String): Flow<Boolean> = watchlistDao.isInWatchlist(id)

    suspend fun toggleWatchlist(item: MediaItem) {
        val entity = WatchlistEntity(
            id = item.id,
            title = item.title,
            type = item.type.name,
            posterUrl = item.posterUrl,
            releaseYear = item.releaseYear,
            rating = item.rating
        )
        // If present, remove; else insert
        watchlistDao.insert(entity)
    }

    suspend fun removeFromWatchlist(id: String) {
        watchlistDao.deleteById(id)
    }

    // Watch history
    fun getWatchHistory(): Flow<List<WatchHistoryEntity>> = watchHistoryDao.getAll()

    suspend fun recordWatch(item: MediaItem) {
        val entity = WatchHistoryEntity(
            id = item.id,
            title = item.title,
            type = item.type.name,
            posterUrl = item.posterUrl,
            releaseYear = item.releaseYear,
            rating = item.rating
        )
        watchHistoryDao.insert(entity)
    }

    suspend fun deleteHistoryItem(id: String) {
        watchHistoryDao.deleteById(id)
    }

    suspend fun clearHistory() {
        watchHistoryDao.clearAll()
    }

    // Continue watching
    fun getContinueWatching(): Flow<List<ContinueWatchingEntity>> = continueWatchingDao.getAll()

    suspend fun saveContinueWatching(
        mediaItem: MediaItem,
        episode: Episode? = null,
        seasonNumber: Int? = null,
        progressMs: Long,
        durationMs: Long
    ) {
        val id = if (episode != null) "${mediaItem.id}_ep_${episode.id}" else mediaItem.id
        val entity = ContinueWatchingEntity(
            id = id,
            mediaId = mediaItem.id,
            title = mediaItem.title,
            episodeTitle = episode?.title,
            posterUrl = mediaItem.posterUrl,
            backdropUrl = mediaItem.backdropUrl,
            progressMs = progressMs,
            durationMs = durationMs,
            mediaType = mediaItem.type.name,
            seasonNumber = seasonNumber,
            episodeNumber = episode?.episodeNumber,
            streamUrl = episode?.streamUrl ?: mediaItem.streamUrl,
            lastWatchedAt = System.currentTimeMillis()
        )
        continueWatchingDao.insert(entity)
    }

    suspend fun removeContinueWatching(id: String) {
        continueWatchingDao.deleteById(id)
    }

    // Search History
    fun getRecentSearches(): Flow<List<String>> = recentSearchDao.getRecent()

    suspend fun saveRecentSearch(query: String) {
        if (query.isNotBlank()) {
            recentSearchDao.insert(RecentSearchEntity(query = query.trim()))
        }
    }

    suspend fun deleteRecentSearch(query: String) {
        recentSearchDao.delete(query)
    }

    suspend fun clearRecentSearches() {
        recentSearchDao.clearAll()
    }
}
