# Meccanismi di estendibilità

Il progetto è stato pensato perché le estensioni più probabili si realizzino **aggiungendo**
codice o dati, non modificando ciò che già esiste. Di seguito gli scenari principali.

## Aggiungere una nuova storia

Una storia è solo un file di dati. Per aggiungerne una basta creare
`src/main/resources/stories/<nuovo-id>.json` con scene, scelte, requisiti ed effetti, e
aggiungere una voce nel manifesto `index.json`. Nello stesso file si possono definire — sempre
come dati — la curva di crescita (`leveling`), i personaggi non giocanti (`npcs`) e gli
scontri (`encounter` sulle scene): livelli, esperienza e combattimenti si configurano così
senza toccare il codice. Non è necessario scrivere o ricompilare alcuna classe: la nuova
avventura comparirà automaticamente nella schermata di nuova partita.

## Aggiungere un nuovo tipo di effetto o requisito

Effetti e requisiti sono i punti di estensione previsti dal principio Open/Closed. Per un
nuovo effetto si crea una classe che implementa `Effect` (definendo `applyTo` e `describe`)
e si aggiunge un `case` nel `EffectDeserializer` per riconoscerne il `"type"` nel JSON. Il
motore non cambia: continua a invocare `applyTo` su un'astrazione. Lo stesso vale per i
requisiti con `Requirement` e `RequirementDeserializer`. È esattamente così che sono nati
`GainExperienceEffect` (`"GAIN_XP"`) e `MinLevelRequirement` (`"MIN_LEVEL"`): una classe più
una riga nel rispettivo deserializzatore, senza alcuna modifica al motore.

## Cambiare il sistema di persistenza

Poiché il resto del codice dipende dalle interfacce `StoryRepository` e `GameRepository`,
si può sostituire il salvataggio su file locale con un database o un servizio remoto
semplicemente fornendo nuove implementazioni di quelle interfacce (per esempio
`DatabaseGameRepository` o `RemoteStoryRepository`) e istanziandole in `GameApp`. Nessuna
modifica al motore, al modello o all'interfaccia grafica.

## Cambiare o aggiungere un'interfaccia utente

Il motore (`GameEngine`) è completamente indipendente da JavaFX e comunica con la
presentazione tramite eventi (`GameEventListener`). Lo stesso motore può quindi essere
riusato da:

- un'interfaccia a riga di comando (un osservatore che stampa scene e legge scelte da
  tastiera);
- una futura applicazione **mobile** o **web**, in cui solo il livello di presentazione
  cambia, mentre dominio, motore e persistenza restano gli stessi.

Questa separazione è la ragione per cui il punto di ingresso (`Main`) e l'applicazione
JavaFX (`GameApp`) sono tenuti distinti dal resto: l'interfaccia è un dettaglio
sostituibile, non il cuore del sistema.

## Riepilogo

| Estensione desiderata            | Cosa si aggiunge                                  | Cosa si modifica        |
|----------------------------------|---------------------------------------------------|-------------------------|
| Nuova storia                     | Un file JSON + voce nel manifesto                 | Nulla                   |
| Nuovo effetto / requisito        | Una classe + un `case` nel deserializzatore       | Solo il deserializzatore|
| Nuova persistenza (DB/remoto)    | Una nuova implementazione del repository          | Una riga in `GameApp`   |
| Nuova interfaccia (CLI/mobile)   | Un nuovo livello di presentazione                 | Nulla nel nucleo        |
