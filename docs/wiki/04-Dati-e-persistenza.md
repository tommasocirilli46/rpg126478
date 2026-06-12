# Organizzazione dei dati e persistenza

Il gioco distingue nettamente due tipi di dati: il **contenuto** (le storie, in sola
lettura) e lo **stato di gioco** (i salvataggi, in lettura e scrittura). Entrambi sono
rappresentati in JSON, ma seguono percorsi diversi.

## Le storie (contenuto)

Una storia è un file JSON che descrive l'intero grafo dell'avventura. Le storie sono
incluse come risorse del progetto sotto `src/main/resources/stories/`. Un manifesto
(`index.json`) elenca le storie disponibili; ogni storia vive in `stories/<id>.json`.

La struttura di una storia comprende metadati (`id`, `title`, `description`), la scena
iniziale (`startSceneId`), il profilo di partenza del personaggio (`start`: salute,
attributi, oggetti), la curva di crescita (`leveling`: esperienza per livello, bonus agli
attributi, livello massimo), il registro dei personaggi non giocanti (`npcs`) e l'elenco
delle scene. Ogni scena contiene il testo e le scelte; ogni scelta indica la scena di
destinazione, i requisiti e gli effetti. Una scena può inoltre dichiarare un `encounter`
(combattimento contro un PNG del registro, con scene di vittoria/fuga ed esperienza in
palio).

Effetti e requisiti sono **polimorfici**: nel JSON sono oggetti con un campo
discriminatore `"type"`. Gli effetti disponibili sono `"MODIFY_HEALTH"`,
`"MODIFY_ATTRIBUTE"`, `"ADD_ITEM"` e `"GAIN_XP"`; i requisiti sono `"HAS_ITEM"`,
`"MIN_ATTRIBUTE"`, `"MIN_HEALTH"` e `"MIN_LEVEL"`. In fase di lettura, `EffectDeserializer`
e `RequirementDeserializer` traducono ciascun oggetto nell'implementazione Java
corrispondente. In questo modo il formato di memorizzazione resta semplice e leggibile,
mentre il dominio lavora con oggetti fortemente tipizzati.

Esempio sintetico di una scelta:

```json
{
  "text": "Apri la porta di ferro a nord",
  "target": "guardiano",
  "requirements": [ { "type": "HAS_ITEM", "itemId": "chiave_arrugginita" } ],
  "effects": [ { "type": "MODIFY_HEALTH", "amount": -5 } ]
}
```

## I salvataggi (stato di gioco)

Quando il giocatore salva, il motore produce un `GameState`: il nome del salvataggio,
l'identificatore della storia, la scena corrente, lo stato completo del personaggio e il
momento del salvataggio. Questo oggetto viene serializzato in JSON e scritto su disco.

I salvataggi risiedono nella cartella personale dell'utente, in
`<home>/.narrative-rpg/saves/`, un file per salvataggio. Conservarli fuori dal progetto
evita di "sporcare" il repository e rende i salvataggi personali per ciascun utente.

Per robustezza, l'elenco e il caricamento dei salvataggi si basano sul nome memorizzato
*dentro* il file, non sul nome del file: così un file rinominato o un nome con caratteri
speciali non compromettono il funzionamento, e un eventuale file corrotto viene ignorato
senza bloccare l'intero elenco.

## Il ruolo del pattern Repository

L'accesso ai dati passa sempre attraverso le interfacce `StoryRepository` e
`GameRepository`. Il resto del programma non sa — e non deve sapere — se i dati arrivino
da un file, da un database o da un servizio remoto. È proprio questa indirezione a rendere
il sistema facilmente estendibile, come descritto nella pagina successiva.
