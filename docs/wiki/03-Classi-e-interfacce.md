# Classi e interfacce

Questa pagina descrive i tipi principali, raggruppati per livello.

## Modello — `it.rpg.model`

**`Character`** — il personaggio del giocatore: nome, salute (con valore massimo),
attributi (mappa nome→valore), inventario, livello ed esperienza. Espone operazioni
coerenti per modificarsi, ad esempio `modifyHealth`, che mantiene sempre la salute tra 0 e
il massimo, e `addExperience`, che accumula soltanto i punti grezzi. Non contiene logica di
gioco: decide *come* cambiare il proprio stato, non *quando* — in particolare non sa cosa
significhi "salire di livello", responsabilità affidata a `LevelingPolicy`.

**`Item`** — un oggetto trasportabile, identificato da un `id`. Due oggetti con lo stesso
`id` sono considerati uguali.

**`Npc`** — un personaggio non giocante definito dalla storia: identificatore, nome,
descrizione, salute massima e potenza d'attacco. È pura *informazione* immutabile,
dichiarata una sola volta nel registro dello `StoryGraph` e referenziata per `id`.

**`Scene`** — un nodo della storia: identificatore, titolo, testo narrativo, elenco di
scelte, esito (`Outcome`), eventuale flag di "scena finale" ed eventuale `Encounter`. Una
scena con un incontro è uno scontro: le sue scelte sono sostituite dalle azioni di
combattimento finché la battaglia non è risolta.

**`Encounter`** — trasforma una scena in una battaglia: indica il `Npc` da affrontare (per
`id`), la scena in cui proseguire alla vittoria, l'eventuale scena raggiunta fuggendo,
l'esperienza assegnata vincendo e quale attributo del giocatore determina il danno inflitto.

**`Choice`** — una diramazione da una scena: testo, identificatore della scena di
destinazione, elenco di requisiti che la abilitano ed elenco di effetti applicati quando
viene scelta.

**`LevelingPolicy`** — la curva di crescita propria di ogni storia: l'esperienza necessaria
per livello, i bonus agli attributi concessi a ogni passaggio di livello e il livello
massimo. Espone `applyTo(Character)`, che promuove il personaggio finché l'esperienza lo
consente. Una storia senza curva equivale a una politica neutra (nessuna progressione).

**`StoryGraph`** — l'intera avventura: metadati, profilo iniziale del personaggio
(`StartProfile`), curva di crescita (`LevelingPolicy`), registro dei `Npc` e tutte le scene
indicizzate per `id`. Le storie sono pura *informazione*.

**`GameState`** — l'istantanea salvabile di una partita: quale storia, scena corrente,
stato completo del personaggio (incluse esperienza e livello) e momento del salvataggio.

**`Outcome`** — enumerazione dell'esito di una scena: `NONE`, `VICTORY`, `DEFEAT`.

### Effetti e requisiti

**`Effect`** (interfaccia, `model.effect`) — una conseguenza applicata al personaggio.
Implementazioni incluse: `ModifyHealthEffect`, `ModifyAttributeEffect`, `AddItemEffect`,
`GainExperienceEffect`.

**`Requirement`** (interfaccia, `model.requirement`) — una precondizione che il personaggio
deve soddisfare perché una scelta sia selezionabile. Implementazioni incluse:
`HasItemRequirement`, `MinAttributeRequirement`, `MinHealthRequirement`, `MinLevelRequirement`.

## Motore — `it.rpg.engine`

**`GameEngine`** (interfaccia) — il contratto contro cui programma l'interfaccia grafica:
avviare una nuova partita, ripristinarne una salvata, leggere scena e personaggio
correnti, sapere se una scelta è selezionabile, compiere una scelta, condurre un
combattimento (`inCombat`, `combat`, `attack`, `flee`), conoscere l'esperienza mancante al
prossimo livello, sapere se la partita è finita e con quale esito, produrre un'istantanea
salvabile, gestire gli osservatori.

**`StoryGameEngine`** — l'implementazione concreta, guidata da uno `StoryGraph`. Valida le
scelte (appartenenza alla scena corrente e requisiti soddisfatti), applica gli effetti e la
curva di esperienza, avvia lo scontro quando si entra in una scena con un `Encounter`,
avanza alla scena successiva e notifica gli osservatori.

**`Combat`** — una singola battaglia tra il personaggio e un `Npc`. Possiede la salute
volatile del nemico (la definizione del `Npc` resta immutabile) e la risolve un round alla
volta in modo **deterministico**: nessuna casualità, così la logica rispecchia il resto
delle regole ed è verificabile in isolamento.

**`CombatRound`** — il risultato immutabile di un round (danno inflitto e subito, salute di
nemico e giocatore), passato agli osservatori perché l'interfaccia mostri l'esito senza
accedere agli interni del combattimento.

**`GameEventListener`** (interfaccia) — l'osservatore degli eventi del motore, con metodi
`default` per i singoli eventi: ingresso in una scena, cambiamento del personaggio, fine
partita, inizio/round/fine di un combattimento e passaggio di livello.

## Persistenza — `it.rpg.persistence`

**`StoryRepository`** / **`GameRepository`** (interfacce) — accesso, rispettivamente, alle
storie disponibili e ai salvataggi.

**`JsonStoryRepository`** — carica le storie dalle risorse JSON sul classpath, configurando
Gson con gli adattatori polimorfici per `Effect` e `Requirement`.

**`JsonGameRepository`** — salva e carica le partite come file JSON nella cartella personale
dell'utente.

**`EffectDeserializer`** / **`RequirementDeserializer`** — traducono il discriminatore
`"type"` del JSON nell'implementazione corretta di `Effect`/`Requirement`.

**`StoryInfo`** / **`SaveInfo`** — piccoli descrittori usati per popolare gli elenchi di
selezione. **`PersistenceException`** — eccezione unchecked del livello di persistenza.

## Interfaccia — `it.rpg.ui`

**`GameApp`** — l'applicazione JavaFX; crea le dipendenze concrete e gestisce la navigazione
tra le schermate. **`MainMenuView`**, **`NewGameView`**, **`LoadGameView`**, **`GameView`** —
le singole schermate. `GameView` è anche un `GameEventListener` e si aggiorna in risposta
agli eventi del motore.

## Avvio — `it.rpg.Main`

**`Main`** — punto di ingresso. Non estende `Application`: avvia JavaFX da una classe
separata così che l'applicazione parta dal classpath senza bisogno di `module-info.java`.
