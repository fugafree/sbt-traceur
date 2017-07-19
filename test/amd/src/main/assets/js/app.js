import { Man } from "./man.js";
import { Woman } from "./woman.js";

const scarlett = new Woman("Scarlett", "Johansson", new Date("1984-11-22"));
const will = new Man("Will", "Smith", new Date("1968-09-25"));
const humans = [scarlett, will];
const $list = document.querySelector("#humans-list");

humans.forEach(function(human) {
    const msg = human.sayHello();
    const $li = document.createElement("li");
    const $text = document.createTextNode(msg);

    $li.appendChild($text);
    $list.appendChild($li);
});