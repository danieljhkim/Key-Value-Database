package cmd

import (
	"fmt"
	"os"

	"github.com/danieljhkim/kv/internal/client"
	"github.com/spf13/cobra"
)

var connectCmd = &cobra.Command{
    Use:   "connect",
    Short: "Connect to a KV database server in interactive mode",
    Long: `Connect to a KV database server using the specified host and port in interactive mode.
    
Example:
  kv connect --host 127.0.0.1 --port 7379`,
    Run: func(cmd *cobra.Command, args []string) {
        host, _ := cmd.Flags().GetString("host")
        port, _ := cmd.Flags().GetInt("port")

        fmt.Printf("Connecting to KV server at %s:%d\n", host, port)
        
        kv := client.NewClient()
        if !kv.Connect(host, port) {
            fmt.Println("Failed to connect to the server")
            os.Exit(1)
        }
        
        kv.RunCLI()
        kv.Disconnect()
    },
}

func init() {
    rootCmd.AddCommand(connectCmd)

    connectCmd.Flags().StringP("host", "H", "127.0.0.1", "Server hostname or IP address")
    connectCmd.Flags().IntP("port", "p", 7379, "Server port")
}