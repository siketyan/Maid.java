const progress = document.getElementById("progress");
const selector = document.getElementById("file");
const learn = document.getElementById("learn");
const generate = document.getElementById("generate");
const result = document.getElementById("result");

const reader = new FileReader();

learn.addEventListener("click", () => {
    reader.readAsText(selector.files[0]);
});

reader.addEventListener("load", () => {
    progress.innerText = "Parsing CSV";
    const csv = Papa.parse(reader.result, {
        header: true
    });

    learnNext(csv.data);
});

let current = 0;
function learnNext(data) {
    const total = data.length;
    const text = data[++current].text;
    progress.innerText = "Learning " + current + " of " + total;

    fetch("/", {
        method: "POST",
        body: encodeURIComponent(text)
    })
        .then(() => {
            learnNext(data);
        });
}

generate.addEventListener("click", () => {
    result.innerText = "Generating";

    fetch("/api")
        .then(response => response.text())
        .then(encoded => decodeURIComponent(encoded))
        .then(text => {
            result.innerText = text;
        });
});
