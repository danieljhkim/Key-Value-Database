package cmd

import (
	"fmt"
	"strings"

	"github.com/spf13/cobra"
)

var setCmd = &cobra.Command{
	Use:   "set [key] [value]",
	Short: "Set key to hold string value",
	Args:  cobra.MinimumNArgs(2),
	Run: func(cmd *cobra.Command, args []string) {
		key := args[0]
		value := strings.Join(args[1:], " ")
		response, err := kvClient.ExecuteCommand(fmt.Sprintf("KV SET %s %s", key, value))
		if err != nil {
			fmt.Printf("Error: %v\n", err)
			return
		}
		fmt.Println(response)
	},
}

func init() {
	rootCmd.AddCommand(setCmd)

	// Here you will define your flags and configuration settings.

	// Cobra supports Persistent Flags which will work for this command
	// and all subcommands, e.g.:
	// saveCmd.PersistentFlags().String("foo", "", "A help for foo")

	// Cobra supports local flags which will only run when this command
	// is called directly, e.g.:
	// saveCmd.Flags().BoolP("toggle", "t", false, "Help message for toggle")
}
