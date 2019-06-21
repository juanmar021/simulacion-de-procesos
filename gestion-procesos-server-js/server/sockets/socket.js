const { io } = require('../server');

const { cServer } = require('../server');

var cliente = undefined;
const net = require('net');




//SOCKET PARA CLIENTES ANGULAR -----------------------------------------------------------
io.on('connection', (client) => {

    console.log('Usuario Angular Conectado');

    cliente = client;

    //console.log(cliente);
    client.emit('enviarMensaje', {
        tipo: 'INFO',
        mensaje: 'Conectado al servidor'
    });




    client.on('disconnect', () => {
        console.log('Usuario Angular desconectado');

    });

    // Escuchar el cliente
    client.on('enviarMensaje', (data, callback) => {

        console.log(data);

        client.broadcast.emit('enviarMensaje', data);


    });

});


//SOCKET PARA CLIENTES JAVA
const serNet = net.createServer((c) => {
    // 'connection' listener
    console.log('Cliente JAVA Conectado');

    cliente.emit('enviarMensaje', {
        tipo: 'INFO',
        mensaje: 'Se conecto usuario java'
    });


    c.on('end', () => {
        console.log('Cliente Java Desconectado');
        cliente.emit('enviarMensaje', {
            tipo: 'INFO',
            mensaje: 'Se desconecto usuario java'
        });
    });


    c.on("getDoc", docId => {
        safeJoin(docId);
        c.emit("document", "{}");
    });



    c.on('data', (data) => {



        console.log("RECIBIENDO DATOSSS...")



        let bufferOne = Buffer.from(data);
        let mensaje = bufferOne.toString('utf8');
        console.log(mensaje);

        var obj = JSON.parse(mensaje);

        if (obj.estado_simulacion != undefined) {
            // console.log(obj);

            cliente.emit('cambiarEstado', {
                tipo: "INFO",
                estado: obj.estado_simulacion
            });

        } else {
            cliente.emit('enviarMensaje', {
                tipo: "DATA",
                proceso: obj
            });
        }




    });


    c.on('error', (err) => {
        console.log(err);
        //throw err;
    });
    c.write('Mensaje del servidor');
    c.pipe(c);
});


serNet.listen(3001, () => {
    console.log('Server para JAVA corriendo 3001');
});