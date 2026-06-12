# Funzionalità

Narrative RPG è un'avventura testuale a scelte. Il gioco presenta una storia organizzata
in *scene*: ogni scena mostra un brano di narrazione e un insieme di scelte possibili.
Selezionando una scelta il giocatore si sposta in una nuova scena e ne subisce gli
effetti sul proprio personaggio, fino a raggiungere un finale di vittoria o di sconfitta.

## Funzionalità principali

**Nuova partita.** Dal menu principale il giocatore può iniziare una nuova avventura,
scegliendo il nome dell'eroe e la storia da affrontare tra quelle disponibili. Il
personaggio iniziale (salute, attributi e oggetti di partenza) è definito dalla storia
stessa.

**Esplorazione a scelte.** Durante il gioco vengono mostrati il titolo della scena, il
testo narrativo e l'elenco delle scelte. Le scelte i cui requisiti non sono soddisfatti
(per esempio "richiede una chiave" o "richiede coraggio ≥ 3") appaiono disabilitate, con
un suggerimento che spiega il requisito mancante.

**Gestione del personaggio.** Un pannello laterale mostra in tempo reale la salute
attuale, il livello, l'esperienza, gli attributi e l'inventario. Effetti come la perdita
di salute, l'aumento di un attributo, l'acquisizione di un oggetto o il guadagno di
esperienza vengono applicati immediatamente quando si compie una scelta.

**Progressione a livelli ed esperienza.** Alcune scelte (e la vittoria nei combattimenti)
assegnano punti esperienza. Quando l'esperienza raggiunge la soglia prevista dalla storia,
il personaggio **sale di livello** e riceve i bonus agli attributi definiti dalla curva di
crescita dell'avventura. Il livello può a sua volta diventare un **requisito** che sblocca
determinate scelte.

**Combattimenti contro i PNG.** Le storie possono definire personaggi non giocanti con
una propria salute e potenza d'attacco. Una scena può ospitare uno **scontro**: al posto
delle scelte compaiono il pannello del nemico e le azioni di combattimento (*Attacca*,
ed eventualmente *Fuggi*). Lo scontro si risolve a turni in modo **deterministico**;
vincere fa proseguire la storia e assegna esperienza, mentre la sconfitta — o l'azzeramento
della salute — porta alla fine della partita. Durante un combattimento il salvataggio è
disabilitato.

**Condizioni di fine partita.** La partita termina quando si raggiunge una scena di
finale (vittoria) oppure quando la salute del personaggio scende a zero (sconfitta). Una
schermata conclusiva comunica l'esito e permette di iniziare una nuova partita o tornare
al menu.

**Salvataggio e caricamento.** In qualsiasi momento la partita può essere salvata con un
nome scelto dall'utente. Dal menu è possibile rivedere l'elenco dei salvataggi (ordinati
per data), riprenderne uno o eliminarlo. I dati vengono conservati localmente in formato
JSON.

## La storia inclusa

Il gioco include la storia *"La Cripta Dimenticata"*: l'eroe esplora una cripta sigillata
in cerca del tesoro. Lungo il percorso accumula esperienza, può trovare una chiave e un
amuleto, e di fronte al **guardiano scheletrico** può scegliere tra più vie: mostrare
l'amuleto per un patto pacifico, placarlo con l'intelligenza, sfruttare l'esperienza
accumulata (una scelta che **richiede il livello 2**) oppure **affrontarlo in
combattimento** — col rischio di soccombere alle ferite. La storia mostra in pratica tutti
i meccanismi del gioco: requisiti su oggetti, attributi e livello; effetti sulla salute,
sull'inventario e sull'esperienza; combattimento contro un PNG; percorsi alternativi e
finali multipli.
