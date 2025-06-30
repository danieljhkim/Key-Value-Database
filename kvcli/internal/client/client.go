package client

import (
	"bufio"
	"errors"
	"fmt"
	"io"
	"net"
	"os"
	"strings"
	"time"
)

const (
	Prompt = "> "
	EndMarker = "END_MARKER"
	WelcomeMessage = "Welcome to the KV CLI! Type 'HELP' for available commands."
	LineSeparator = "----------------------------------------"

	KVHelpMessage = `KV-Store Available commands:
0. KV CONNECT --host <host> --port <port>  - Connect to a KV server.
1. KV SET key value - Store a key-value pair.
2. KV GET key - Retrieve the value for a given key.
3. KV DEL key - Remove a key-value pair.
4. KV DROP - Remove all entries.
5. KV PING - Check connection.
6. KV QUIT/EXIT - Close the connection.
7. KV HELP - Display this help message.`
)

type Client struct {
	host      string
	port      int
	conn      net.Conn
	reader    *bufio.Reader
	writer    *bufio.Writer
	connected bool
	welcomeDisplayed bool
}

func NewClient() *Client {
	return &Client{}
}

func (c *Client) Connect(host string, port int) bool {
	if c.IsConnected() {
		c.Disconnect()
	}

	c.host = host
	c.port = port

	addr := fmt.Sprintf("%s:%d", host, port)
	conn, err := net.Dial("tcp", addr)
	if err != nil {
		fmt.Printf("Failed to connect: %v\n", err)
		return false
	}

	c.conn = conn
	c.reader = bufio.NewReader(conn)
	c.writer = bufio.NewWriter(conn)
	c.connected = true

	// fmt.Println(WelcomeMessage)
	// fmt.Printf("Connected to KV Database on %s:%d\n", host, port)
	// fmt.Println(LineSeparator)
	// fmt.Println(KVHelpMessage)
	// fmt.Println(LineSeparator)

	return true
}

func (c *Client) RunCLI() {
	if !c.IsConnected() {
		fmt.Println("Error: Not connected to server. Please connect first.")
		return
	}

	scanner := bufio.NewScanner(os.Stdin)

	for {
		fmt.Print(Prompt)
		if !scanner.Scan() {
			break
		}
		command := strings.TrimSpace(scanner.Text())
		if command == "exit" || command == "quit" {
			break
		}
		if command == "" {
			continue
		}

		response, err := c.ExecuteCommand(command)
		if err != nil {
			fmt.Printf("Error: %v\n", err)
			if errors.Is(err, io.EOF) {
				c.Disconnect()
				break
			}
		}
		fmt.Println(response)
	}
}

func (c *Client) ExecuteCommand(commandString string) (string, error) {
	if !c.IsConnected() {
		return "", errors.New("not connected to server")
	}
	// Send command
	if _, err := c.writer.WriteString(commandString + "\n"); err != nil {
		return "", err
	}
	if err := c.writer.Flush(); err != nil {
		return "", err
	}

	// Set read timeout
	if err := c.conn.SetReadDeadline(time.Now().Add(3 * time.Second)); err != nil {
		return "", err
	}

	var response strings.Builder
	for {
		line, err := c.reader.ReadString('\n')
		if err != nil {
			if netErr, ok := err.(net.Error); ok && netErr.Timeout() {
				fmt.Fprintln(os.Stderr, "Server response timed out.")
				break
			}
			return "", err
		}

		line = strings.TrimSuffix(line, "\n")
		if line == EndMarker {
			break
		}
		if response.Len() > 0 {
			response.WriteString("\n")
		}
		response.WriteString(line)

		// Check if more data is available
		if c.reader.Buffered() == 0 {
			break
		}
	}

	// Clear read deadline
	c.conn.SetReadDeadline(time.Time{})

	return response.String(), nil
}

func (c *Client) Disconnect() {
	if c.IsConnected() {
		c.conn.Close()
		fmt.Println("Disconnected from server.")
	}
	c.conn = nil
	c.reader = nil
	c.writer = nil
	c.connected = false
}

func (c *Client) IsConnected() bool {
	return c.connected && c.conn != nil
}