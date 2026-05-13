# Narrative RPG — La Cripta Dimenticata

Gioco di ruolo **narrativo a scelte** (avventura testuale interattiva) realizzato in
**Java 25** con interfaccia grafica **JavaFX**. Il giocatore impersona un eroe che
esplora una cripta: ogni scena propone un testo e una serie di scelte; alcune scelte
richiedono oggetti, attributi o un livello minimo, altre modificano salute, attributi,
inventario ed esperienza. L'eroe **sale di livello** accumulando XP e può **affrontare
in combattimento** i personaggi non giocanti (PNG) della storia. Lo stato della partita
può essere **salvato e ripreso** in qualsiasi momento.

Il progetto è costruito secondo i principi **SOLID** e con un'attenzione esplicita
all'**estendibilità** (nuove storie, nuovi effetti/requisiti e perfino una nuova
interfaccia o un diverso sistema di persistenza si aggiungono senza modificare il
codice esistente).

## Requisiti

- **JDK 25** (consigliato). Se non è installato, Gradle è configurato per scaricarne
  automaticamente uno compatibile tramite il *foojay toolchains resolver*.
- Connessione a Internet **al primo avvio** (per scaricare Gradle, JavaFX e Gson).

## Compilazione ed esecuzione — due soli comandi

```bash
./gradlew build      # compila il progetto ed esegue i test
./gradlew run        # avvia il gioco
```

Su Windows usare `gradlew.bat build` e `gradlew.bat run`.

> **Nota sul Gradle Wrapper.** Il repository contiene gli script `gradlew`/`gradlew.bat`
> e il file `gradle/wrapper/gradle-wrapper.properties`. Manca soltanto il binario
> `gradle/wrapper/gradle-wrapper.jar`, che non può essere distribuito come testo.
> Lo si genera **una sola volta** in uno di questi modi:
>
> - **Con IntelliJ IDEA:** all'apertura del progetto, IntelliJ riconosce Gradle e, alla
>   prima sincronizzazione ("Sync"), crea automaticamente il file `gradle-wrapper.jar`.
> - **Da terminale (se Gradle è installato):** `gradle wrapper --gradle-version 9.1.0`
>
> Dopo questo passaggio il `.jar` va incluso nel **primo commit**, così che i due comandi
> sopra funzionino su qualsiasi computer.

## Struttura del progetto

```
src/main/java/it/rpg/
├── Main.java                 # launcher (non estende Application)
├── model/                    # entità di dominio (Character, Scene, Choice, Npc, …)
│   ├── effect/               # Effect + implementazioni (punto di estensione)
│   └── requirement/          # Requirement + implementazioni (punto di estensione)
├── engine/                   # GameEngine + StoryGameEngine + Combat + eventi (observer)
├── persistence/              # Repository (interfacce) + implementazioni JSON
└── ui/                       # interfaccia JavaFX (menu, gioco, salvataggi)

src/main/resources/
├── stories/                  # le storie, come dati JSON
└── css/                      # tema grafico
```

## Dove vengono salvate le partite

I salvataggi sono file JSON nella cartella personale dell'utente:
`<home>/.narrative-rpg/saves/`. Non vengono inclusi nel repository.

## Dichiarazione sull'uso dell'IA

Vedi il file [`docs/wiki/06-Dichiarazione-uso-IA.md`](docs/wiki/06-Dichiarazione-uso-IA.md)
per la dichiarazione dettagliata sull'utilizzo di strumenti di intelligenza artificiale
durante lo sviluppo del progetto.
