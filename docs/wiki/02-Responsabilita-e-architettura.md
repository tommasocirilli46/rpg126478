# Responsabilità e architettura

Il sistema è suddiviso in livelli con responsabilità ben distinte. Ogni livello dipende
solo da astrazioni dei livelli sottostanti, mai da implementazioni concrete dei livelli
superiori. Il risultato è un nucleo di gioco indipendente sia dall'interfaccia grafica sia
dal meccanismo di salvataggio.

## I livelli

**Modello (`it.rpg.model`).** Contiene le entità di dominio: il personaggio (con salute,
attributi, inventario, livello ed esperienza), gli oggetti, i personaggi non giocanti
(`Npc`), le scene — comprese quelle che ospitano uno scontro (`Encounter`) — le scelte, la
curva di crescita (`LevelingPolicy`), la storia nel suo complesso e lo stato salvabile
della partita. È puro dominio: non conosce JavaFX, non conosce Gson, non sa nulla di file
o di finestre. I sottopacchetti `model.effect` e `model.requirement` definiscono le
astrazioni `Effect` e `Requirement` con le rispettive implementazioni. Coerentemente con
questa separazione, `Character` accumula soltanto l'esperienza grezza, mentre *quando* e
*come* si sale di livello è deciso dalla `LevelingPolicy`, applicata dal motore.

**Motore (`it.rpg.engine`).** Contiene la logica di gioco: a partire da una storia,
gestisce la scena corrente e il personaggio, valida e applica le scelte, applica la curva
di esperienza, risolve i **combattimenti** (`Combat`), determina le condizioni di fine
partita e notifica gli osservatori. È completamente indipendente dall'interfaccia: la
stessa logica potrebbe essere usata da una GUI, da una riga di comando o da un test. La
risoluzione del combattimento è **deterministica**, così resta facilmente verificabile con
gli unit test.

**Persistenza (`it.rpg.persistence`).** Definisce le interfacce `StoryRepository` (lettura
delle storie) e `GameRepository` (salvataggio/caricamento delle partite) e ne fornisce
implementazioni basate su file JSON locali. Tutta la conoscenza relativa alla
serializzazione è confinata qui.

**Interfaccia (`it.rpg.ui`).** Contiene l'applicazione JavaFX e le sue schermate. Dipende
dall'astrazione `GameEngine` e dalle interfacce dei repository, non dalle loro
implementazioni concrete.

## Applicazione dei principi SOLID

**Single Responsibility.** Ogni classe ha un solo motivo per cambiare: `Character`
gestisce lo stato del personaggio, `StoryGameEngine` la progressione del gioco,
`JsonGameRepository` la lettura/scrittura su disco, ogni *view* una singola schermata.

**Open/Closed.** Effetti e requisiti sono i principali punti di estensione: si aggiunge un
nuovo tipo creando una nuova implementazione di `Effect` o `Requirement`, senza modificare
il motore. Anche le storie sono dati esterni: una nuova avventura non richiede codice.

**Liskov Substitution.** Ogni implementazione di `Effect`, `Requirement`, `GameEngine` o
dei repository è pienamente sostituibile alla propria interfaccia; il chiamante non ha mai
bisogno di sapere quale implementazione concreta sta usando.

**Interface Segregation.** Le interfacce sono piccole e mirate: `StoryRepository` e
`GameRepository` sono separate perché servono scopi diversi; `GameEventListener` espone
metodi `default` così un osservatore implementa solo gli eventi che gli interessano.

**Dependency Inversion.** L'interfaccia grafica programma contro `GameEngine` e contro le
interfacce dei repository; il motore programma contro `Effect`/`Requirement`. Le
dipendenze concrete (`StoryGameEngine`, `JsonStoryRepository`, `JsonGameRepository`) sono
istanziate in un unico punto, la classe `GameApp`, che funge da composizione del sistema.

## Disaccoppiamento tramite eventi (Observer)

Il motore non conosce l'interfaccia grafica. Per aggiornare la schermata di gioco si usa
il pattern *Observer*: la *view* si registra come `GameEventListener` e riceve notifiche
quando lo stato cambia — `onSceneEntered`, `onCharacterChanged`, `onGameOver`, gli eventi
di combattimento (`onCombatStarted`, `onCombatRound`, `onCombatEnded`) e `onLevelUp`. I
metodi sono `default`, perciò aggiungere un nuovo evento non rompe gli osservatori
esistenti, che intercettano solo ciò che li riguarda. In questo modo la logica e la
presentazione restano indipendenti e sostituibili.
